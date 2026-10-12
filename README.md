# theseuntaylor-web

Personal site for Solomon George-Taylor: portfolio, photography, and an arcade of games from my apps. Written entirely in Kotlin.

| Module | What it is |
|---|---|
| `site/` | The portfolio, built with [Kobweb](https://kobweb.varabyte.com) (Compose HTML). Real HTML and CSS, so it's fast and search engines can read it. |
| `arcade/shared/` | Kotlin Multiplatform game logic and the game catalogue. Targets JS (read by the site), Wasm (the arcade), and JVM (so the Android apps can share it). |
| `arcade/web/` | The arcade itself: Compose Multiplatform compiled to WebAssembly. The site build copies it to `/play/`. |

## Run it locally

```bash
./gradlew :site:kobwebStart -t
```

Then open http://localhost:3141. `-t` rebuilds and live-reloads on save. Stop it with `./gradlew :site:kobwebStop`.

## Edit the content

- Copy, projects and contact details: `site/src/jsMain/kotlin/dev/theseuntaylor/site/data/Content.kt`
- Photos: put files in `site/src/jsMain/resources/public/photos/`, then list them in `data/Photos.kt`
- Arcade games: `arcade/shared/src/commonMain/kotlin/dev/theseuntaylor/arcade/ArcadeGame.kt`. Flip `playable` once a game's logic lands.
- Accent colour: `SiteColors.accent` in `SiteTheme.kt`

## Test

```bash
e2e/site.sh
```

This exports the static site exactly as it will be deployed and walks it in Chromium: home, arcade (the Wasm app boots and switching games doesn't reload it), the photography lightbox, and phone width. Screenshots and `result.txt` go to `e2e/artifacts/site/<timestamp>/`.

## Deploy

Every push to `main` exports the site and publishes it with GitHub Pages (`.github/workflows/deploy.yml`). To use the custom domain:

1. In the repo, open **Settings → Pages** and set **Source** to *GitHub Actions*.
2. Buy the domain, then add `site/src/jsMain/resources/public/CNAME` containing just `theseuntaylor.dev`.
3. At the registrar, point the domain at GitHub Pages (A/AAAA records for the apex, or a CNAME to `theseuntaylor.github.io`), then enter it under **Settings → Pages → Custom domain**.

The site uses root-relative links (`/arcade`, `/play/…`), so it needs to be served from a domain root. That means the custom domain, or a repo named `theseuntaylor.github.io`.
