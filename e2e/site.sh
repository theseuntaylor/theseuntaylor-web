#!/usr/bin/env bash
# E2E: export the static site exactly as it will be deployed, serve it, and walk it in a real browser:
# home → work → arcade (Compose Wasm boots, switching games works) → photography (lightbox) → phone width.
# Usage: e2e/site.sh            (rebuilds the export first)
#        SKIP_EXPORT=1 e2e/site.sh   (reuses site/.kobweb/site from the last export)
# Leaves screenshots and result.txt in e2e/artifacts/site/<timestamp>/.
set -euo pipefail

cd "$(dirname "$0")/.."
OUT="e2e/artifacts/site/$(date +%Y%m%d-%H%M%S)"
mkdir -p "$OUT"

if [[ -z "${SKIP_EXPORT:-}" ]]; then
    # Exporting spins up its own Kobweb server on the dev port, so stop any dev server left running.
    ./gradlew :site:kobwebStop -q >/dev/null 2>&1 || true
    ./gradlew :site:kobwebExport -PkobwebReuseServer=false -PkobwebEnv=DEV -PkobwebRunLayout=STATIC \
        -PkobwebBuildTarget=RELEASE -PkobwebExportLayout=STATIC -q
fi
[[ -f site/.kobweb/site/index.html ]] || { echo "FAIL: no export at site/.kobweb/site" | tee "$OUT/result.txt"; exit 1; }

(cd e2e && [[ -d node_modules/playwright ]] || npm install --silent --no-audit --no-fund)
node e2e/check-site.mjs site/.kobweb/site "$OUT"
