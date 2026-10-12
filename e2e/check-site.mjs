// Serves the exported static site the way GitHub Pages does (clean URLs → .html files) and drives it with Playwright.
// Every step leaves a screenshot; the run ends by writing PASS/FAIL and the step log to result.txt.
import { createServer } from "node:http";
import { readFile, stat, writeFile } from "node:fs/promises";
import { extname, join, resolve } from "node:path";
import { chromium } from "playwright";

const [root, out] = process.argv.slice(2).map((p) => resolve(p));
const types = { ".html": "text/html", ".js": "text/javascript", ".wasm": "application/wasm", ".css": "text/css",
    ".png": "image/png", ".jpg": "image/jpeg", ".svg": "image/svg+xml", ".ico": "image/x-icon", ".json": "application/json" };

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
    res.writeHead(200, { "content-type": types[extname(file)] ?? "application/octet-stream" });
    res.end(await readFile(file));
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

    await page.click("a[href='/#work'] >> nth=0");
    await page.waitForTimeout(1200);
    const rows = page.locator("#work a h3");
    expect((await rows.count()) === 4, "home: work list shows 4 projects");
    await rows.nth(1).hover();
    await page.waitForTimeout(400);
    expect(await page.locator("text=open on GitHub ↗").isVisible(), "home: hovering a project shows the trailing preview card");
    await capture("home-work");

    await page.locator("#connect").scrollIntoViewIfNeeded();
    await page.waitForTimeout(1200);
    expect(await page.locator("a[href^='mailto:']").isVisible(), "home: connect section shows the email link");
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
    await page.goto(`${base}/photography`, { waitUntil: "networkidle" });
    const photos = page.locator("[data-photo]");
    const count = await photos.count();
    expect(count >= 6, `photography: gallery lists ${count} photos`);
    await page.waitForTimeout(1200);
    await capture("photography-grid");
    await photos.first().locator("[style*='aspect-ratio']").first().click();
    await page.waitForSelector("[role='dialog']");
    expect((await page.textContent("[role='dialog']")).includes(`1 / ${count}`), "photography: clicking a photo opens the lightbox at 1/N");
    await page.keyboard.press("ArrowRight");
    expect((await page.textContent("[role='dialog']")).includes(`2 / ${count}`), "photography: → moves to the next photo");
    await capture("photography-lightbox");
    await page.keyboard.press("Escape");
    expect((await page.locator("[role='dialog']").count()) === 0, "photography: Escape closes the lightbox");

    // Phone width: nothing should scroll sideways.
    const phone = await browser.newPage({ viewport: { width: 375, height: 812 }, isMobile: true, hasTouch: true });
    for (const path of ["/", "/arcade", "/photography"]) {
        await phone.goto(`${base}${path}`, { waitUntil: "networkidle" });
        await phone.waitForTimeout(800);
        const overflow = await phone.evaluate(() => document.documentElement.scrollWidth - window.innerWidth);
        expect(overflow <= 0, `phone: ${path} has no horizontal scroll (overflow ${overflow}px)`);
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
