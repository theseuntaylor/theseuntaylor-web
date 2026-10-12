// Serves the exported static site the way GitHub Pages does (clean URLs → .html files) and drives it with Playwright.
// Every step leaves a screenshot; the run ends by writing PASS/FAIL and the step log to result.txt.
import { createServer } from "node:http";
import { readFile, stat, writeFile } from "node:fs/promises";
import { extname, join, resolve } from "node:path";
import { chromium } from "playwright";

const [root, out] = process.argv.slice(2).map((p) => resolve(p));
const types = { ".html": "text/html", ".js": "text/javascript", ".wasm": "application/wasm", ".css": "text/css",
    ".png": "image/png", ".jpg": "image/jpeg", ".svg": "image/svg+xml", ".ico": "image/x-icon", ".json": "application/json", ".mp4": "video/mp4" };

async function resolveFile(urlPath) {
    const clean = decodeURIComponent(urlPath.split(/[?#]/)[0]);
    for (const candidate of [clean, `${clean}.html`, join(clean, "index.html")]) {
        const file = join(root, candidate);
        if (!file.startsWith(root)) return null;
        try { if ((await stat(file)).isFile()) return file; } catch {}
    }
    return null;
}

const server = createServer(async (req, res) => {
    const file = await resolveFile(req.url);
    if (!file) { res.writeHead(404).end(); return; }
    const body = await readFile(file);
    const headers = { "content-type": types[extname(file)] ?? "application/octet-stream", "accept-ranges": "bytes" };
    // Video elements fetch in byte ranges (and need them to loop), like they would from GitHub Pages.
    const range = /bytes=(\d*)-(\d*)/.exec(req.headers.range ?? "");
    if (range) {
        const start = range[1] ? Number(range[1]) : 0;
        const end = range[2] ? Math.min(Number(range[2]), body.length - 1) : body.length - 1;
        res.writeHead(206, { ...headers, "content-range": `bytes ${start}-${end}/${body.length}`, "content-length": end - start + 1 });
        res.end(body.subarray(start, end + 1));
        return;
    }
    res.writeHead(200, { ...headers, "content-length": body.length });
    res.end(body);
});
await new Promise((r) => server.listen(0, "127.0.0.1", r));
const base = `http://127.0.0.1:${server.address().port}`;

const log = [];
const errors = [];
let shot = 0;
const step = (msg) => { log.push(`ok   ${msg}`); console.log(`ok   ${msg}`); };
const expect = (cond, msg) => { if (!cond) throw new Error(msg); step(msg); };

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1280, height: 860 } });
page.on("pageerror", (e) => errors.push(`pageerror: ${e.message}`));
page.on("console", (m) => { if (m.type() === "error") errors.push(`console: ${m.text()}`); });
let wasmLoads = 0;
page.on("response", (r) => { if (/\/play\/arcade\.js$/.test(r.url())) wasmLoads++; });
const capture = async (name, p = page) => p.screenshot({ path: join(out, `${++shot}-${name}.png`) });

let failure = null;
try {
    // Home
    await page.goto(`${base}/`, { waitUntil: "networkidle" });
    await page.waitForSelector("h1");
    expect((await page.textContent("h1")).includes("apps you can"), "home: hero headline renders");
    expect((await page.locator(".reveal").count()) > 10, "home: scroll-reveal sections are present");
    await capture("home-hero");

    const media = page.locator("[data-name-block-media]");
    await media.scrollIntoViewIfNeeded();
    await page.waitForTimeout(1200);
    const mediaBox = await media.boundingBox();
    const panelBox = await page.locator("[data-name-block-panel]").boundingBox();
    expect(Math.abs(mediaBox.height - panelBox.height) < 2, `home: name block halves are the same height (${Math.round(mediaBox.height)} vs ${Math.round(panelBox.height)}px)`);
    const edgeLeft = (await page.locator("[data-edge-clock]").boundingBox()).x;
    expect(panelBox.x + panelBox.width <= edgeLeft - 4, "home: the purple panel stops short of the right-edge line");
    const surnameLines = await page.locator("[data-name-block-panel] h3").evaluate((h) => {
        const range = document.createRange(); range.selectNodeContents(h);
        return new Set([...range.getClientRects()].map((r) => Math.round(r.top))).size;
    });
    expect(surnameLines <= 2, `home: the name sits on at most two lines, with George-Taylor kept together (${surnameLines})`);
    expect(Math.abs(mediaBox.width - panelBox.width) < 2, `home: name block splits 50/50 (${Math.round(mediaBox.width)} / ${Math.round(panelBox.width)}px)`);
    const blockLeft = mediaBox.x, blockRight = 1280 - (panelBox.x + panelBox.width);
    expect(Math.abs(blockLeft - blockRight) < 2 && blockLeft > 0, `home: name block has equal space on both sides (${Math.round(blockLeft)} / ${Math.round(blockRight)}px)`);
    await capture("home-name-block");

    await page.click("a[href='/#work'] >> nth=0");
    const rows = page.locator("#work a h3");
    const projectCount = await rows.count();
    expect(projectCount >= 4, `home: work list shows ${projectCount} projects`);
    // Wait for smooth scrolling and the fade-ins to settle, otherwise the hover lands on a moving target.
    await page.waitForFunction((n) => document.querySelectorAll("#work .reveal.in").length >= n, projectCount);
    await page.waitForTimeout(1000);
    await rows.nth(1).scrollIntoViewIfNeeded();
    await rows.nth(1).hover();
    await page.waitForTimeout(400);
    expect(await page.locator("text=open on GitHub ↗").isVisible(), "home: hovering a project shows the trailing preview card");
    await capture("home-work");

    const band = page.locator("h2", { hasText: /^arcade$/ }).locator("xpath=ancestor::div[2]");
    const bandBox = await band.boundingBox();
    expect(Math.abs(bandBox.x - (1280 - bandBox.x - bandBox.width)) < 2, `home: arcade band has equal space on both sides (${Math.round(bandBox.x)} / ${Math.round(1280 - bandBox.x - bandBox.width)}px)`);
    await page.locator("#connect").scrollIntoViewIfNeeded();
    await page.waitForTimeout(1200);
    expect(await page.locator("a[href^='mailto:']").isVisible(), "home: connect section shows the email link");
    const londonHour = () => new Intl.DateTimeFormat("en-GB", { hour: "2-digit", timeZone: "Europe/London" }).format(new Date());
    const edgeClock = page.locator("[data-edge-clock]");
    expect(await edgeClock.isVisible(), "home: location and time sit on the right edge on desktop");
    const edgeText = (await edgeClock.textContent()).trim();
    expect(/^\|\s+london, uk 🇬🇧\s+\|\s+\d{2}:\d{2}:\d{2}$/u.test(edgeText), `home: edge reads "| london, uk 🇬🇧 | HH:MM:SS" ("${edgeText}")`);
    expect(edgeText.slice(-8, -6) === londonHour(), `home: the clock is on London time (expected hour ${londonHour()})`);
    await page.waitForTimeout(1500);
    const later = (await edgeClock.textContent()).trim();
    expect(later !== edgeText, `home: the seconds tick (${edgeText.slice(-8)} → ${later.slice(-8)})`);
    expect(!(await page.locator("[data-connect-clock]").isVisible()), "home: connect section doesn't repeat the location on desktop");
    const wordmarkBox = await page.locator("a:visible", { hasText: "theseuntaylor" }).first().boundingBox();
    const clockBox = await edgeClock.boundingBox();
    const sameLine = Math.abs((clockBox.x + clockBox.width / 2) - (wordmarkBox.x + wordmarkBox.width / 2)) < 12;
    expect(sameLine && clockBox.y >= wordmarkBox.y + wordmarkBox.height - 2,
        "home: location and time continue the wordmark's line, straight after it");
    const toggleBox = await page.locator("[data-color-mode-toggle]:visible").boundingBox();
    expect(Math.abs((toggleBox.x + toggleBox.width / 2) - (wordmarkBox.x + wordmarkBox.width / 2)) < 4 && toggleBox.y + toggleBox.height > 860 - 60,
        `home: the day/night toggle sits at the bottom of the wordmark's column (bottom at ${Math.round(toggleBox.y + toggleBox.height)}px)`);
    await capture("home-connect");

    // Arcade
    const wasm = page.waitForResponse((r) => r.url().endsWith(".wasm"), { timeout: 60_000 });
    await page.click("a[href='/arcade'] >> nth=0");
    await page.waitForURL(/\/arcade/);
    const frame = page.frameLocator("iframe[title='Arcade']");
    await frame.locator("canvas").first().waitFor({ timeout: 30_000 });
    const wasmResponse = await wasm;
    expect(wasmResponse.headers()["content-type"].includes("application/wasm"), "arcade: .wasm is served as application/wasm");
    await page.waitForTimeout(1500);
    step("arcade: Compose Wasm canvas mounted inside the cabinet");
    await capture("arcade-lobby");

    await page.click("[data-game='hexo']");
    await page.waitForTimeout(1500);
    const arcadeFrame = page.frames().find((f) => f.url().includes("/play/"));
    expect((await arcadeFrame.evaluate(() => location.hash)) === "#hexo", "arcade: choosing Hexo routes the running Wasm app to #hexo");
    expect(wasmLoads === 1, `arcade: switching games did not reload the Wasm app (arcade.js loaded ${wasmLoads}x)`);
    expect(page.url().endsWith("/arcade"), "arcade: the outer page stayed on /arcade");
    await capture("arcade-hexo");

    // Photography
    const mp4 = page.waitForResponse((r) => r.url().endsWith(".mp4"), { timeout: 20_000 });
    await page.goto(`${base}/photography`, { waitUntil: "networkidle" });
    const photos = page.locator("[data-photo]");
    const count = await photos.count();
    expect(count >= 6, `photography: gallery lists ${count} photos`);
    await page.waitForTimeout(1200);
    const tile = page.locator("[data-photo-video]").first();
    expect((await tile.count()) === 1 && (await tile.getAttribute("preload")) === "none" && !!(await tile.getAttribute("poster")),
        "photography: the video tile waits behind a poster and downloads nothing up front");
    await tile.scrollIntoViewIfNeeded();
    expect((await mp4).headers()["content-type"] === "video/mp4", "photography: the clip is served as video/mp4");
    await page.waitForFunction(() => { const v = document.querySelector("[data-photo-video]"); return v && !v.paused && v.currentTime > 0.3; }, null, { timeout: 15_000 });
    expect(await tile.evaluate((v) => v.muted && v.loop), "photography: once on screen the clip plays muted and on a loop");
    await capture("photography-grid");
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
    await page.waitForTimeout(800);
    expect(await tile.evaluate((v) => v.paused), "photography: the clip pauses once scrolled out of view");
    await page.evaluate(() => window.scrollTo(0, 0));
    await page.waitForTimeout(600);
    const hd = page.waitForResponse((r) => r.url().endsWith("-hd.mp4"), { timeout: 20_000 });
    await photos.first().locator("[style*='aspect-ratio']").first().click();
    await page.waitForSelector("[role='dialog']");
    expect((await page.textContent("[role='dialog']")).includes(`1 / ${count}`), "photography: clicking a photo opens the lightbox at 1/N");
    await page.keyboard.press("ArrowRight");
    await page.waitForFunction((n) => document.querySelector("[role='dialog']")?.textContent.includes(`2 / ${n}`), count, { timeout: 3000 }).catch(() => {});
    expect((await page.textContent("[role='dialog']")).includes(`2 / ${count}`), "photography: → moves to the next photo");
    await capture("photography-lightbox");
    await page.keyboard.press("Escape");
    expect((await page.locator("[role='dialog']").count()) === 0, "photography: Escape closes the lightbox");

    await page.locator("[data-photo-video]").first().click();
    await page.waitForSelector("[data-lightbox-video]");
    await hd;
    step("photography: the viewer fetches the HD clip only when opened");
    await page.waitForFunction(() => { const v = document.querySelector("[data-lightbox-video]"); return v && !v.paused && v.currentTime > 0.3; }, null, { timeout: 15_000 });
    expect(await page.locator("[data-lightbox-video]").evaluate((v) => v.controls && !v.muted), "photography: opening the clip plays it large, with sound and controls");
    await capture("photography-video-lightbox");
    await page.keyboard.press("Escape");

    // Day / night
    const paperOf = (p) => p.evaluate(() => getComputedStyle(document.body).backgroundColor);
    await page.goto(`${base}/`, { waitUntil: "networkidle" });
    const dayPaper = await paperOf(page);
    expect(dayPaper === "rgb(244, 240, 236)", `day: starts on day paper for a light-mode visitor (${dayPaper})`);
    await page.click("[data-color-mode-toggle]:visible");
    await page.waitForTimeout(600);
    const nightPaper = await paperOf(page);
    expect(nightPaper === "rgb(20, 17, 23)", `night: the moon toggle switches to night paper (${nightPaper})`);
    expect((await page.getAttribute("[data-color-mode-toggle]:visible", "data-color-mode-toggle")) === "dark", "night: the toggle now offers the sun");
    await capture("night-home");
    await page.reload({ waitUntil: "networkidle" });
    expect((await paperOf(page)) === nightPaper, "night: the choice survives a reload");
    await page.goto(`${base}/photography`, { waitUntil: "networkidle" });
    await page.waitForTimeout(1200);
    await capture("night-photography");
    const labelColor = await page.locator("[data-photo-label]").first().evaluate((e) => getComputedStyle(e).color);
    expect(labelColor.startsWith("rgba(38, 38, 38"), `night: photo placeholder labels stay dark on their light tints (${labelColor})`);
    await page.click("[data-color-mode-toggle]:visible");
    await page.waitForTimeout(600);
    expect((await paperOf(page)) === dayPaper, "day: the sun toggle switches back to day");

    const nightOwl = await browser.newPage({ viewport: { width: 1280, height: 860 }, colorScheme: "dark" });
    await nightOwl.goto(`${base}/`, { waitUntil: "networkidle" });
    expect((await paperOf(nightOwl)) === nightPaper, "night: a first-time visitor with a dark system setting starts in night mode");
    await nightOwl.close();

    // Phone width: nothing should scroll sideways.
    const phone = await browser.newPage({ viewport: { width: 375, height: 812 }, isMobile: true, hasTouch: true });
    for (const path of ["/", "/arcade", "/photography"]) {
        await phone.goto(`${base}${path}`, { waitUntil: "networkidle" });
        await phone.waitForTimeout(800);
        // On a real phone, content wider than the screen makes the browser widen its layout viewport and zoom
        // out, so check against the device width rather than innerWidth (which stretches along with it).
        const layoutWidth = await phone.evaluate(() => Math.max(window.innerWidth, document.documentElement.scrollWidth));
        expect(layoutWidth <= 375, `phone: ${path} fits a 375px screen (laid out at ${layoutWidth}px)`);
        if (path === "/") {
            await phone.locator("#connect").scrollIntoViewIfNeeded();
            await phone.waitForTimeout(1200);
            expect(await phone.locator("[data-connect-clock]").isVisible(), "phone: location and time show in the connect section instead");
            expect(/\d{2}:\d{2}:\d{2} local time/.test(await phone.locator("[data-connect-clock]").textContent()), "phone: the connect clock shows seconds too");
            expect(!(await phone.locator("[data-edge-clock]").isVisible()), "phone: the edge clock is hidden");
        }
        const toggle = await phone.locator("[data-color-mode-toggle]:visible").boundingBox();
        expect(toggle && toggle.x >= 0 && toggle.x + toggle.width <= 375, `phone: ${path} day/night toggle is fully on screen (right edge ${toggle && Math.round(toggle.x + toggle.width)}px)`);
        await capture(`phone${path.replace(/\//g, "-") || "-home"}`, phone);
    }

    const realErrors = errors.filter((e) => !e.includes("favicon"));
    expect(realErrors.length === 0, `no page or console errors${realErrors.length ? ": " + realErrors.join(" | ") : ""}`);
} catch (e) {
    failure = e;
    log.push(`FAIL ${e.message}`);
    console.error(`FAIL ${e.message}`);
    await capture("failure").catch(() => {});
} finally {
    await browser.close();
    server.close();
}

await writeFile(join(out, "result.txt"), `${failure ? "FAIL" : "PASS"}\n${log.join("\n")}\n${errors.length ? "\nerrors:\n" + errors.join("\n") + "\n" : ""}`);
console.log(`${failure ? "FAIL" : "PASS"}: artifacts in ${out}`);
process.exit(failure ? 1 : 0);
