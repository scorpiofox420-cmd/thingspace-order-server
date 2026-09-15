#!/usr/bin/env bash
#
# deploy-live.sh — strict PRODUCTION build+run for an offline terminal.
#
#   - No mock mode, ever. THINGSPACE_ENVIRONMENT is forced to PRODUCTION.
#   - Fails immediately if any credential is empty or still a placeholder.
#   - Builds with the repo-local Maven (.deploy-tools/) using the offline
#     .m2/ cache, so no internet is needed once the cache is populated
#     (first build MUST have internet: run ./deploy.sh build once online).
#   - Requires a reachable Postgres (DB_HOST/DB_PORT/DB_USER/DB_PASSWORD).
#
# Usage:
#   ./deploy-live.sh          # build (offline) + run, strict checks
#   ./deploy-live.sh build    # build only
#   ./deploy-live.sh run      # run only (skips build)
#
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

DEPLOY_TOOLS="$PROJECT_DIR/.deploy-tools"
MAVEN_BIN="$DEPLOY_TOOLS/apache-maven-3.9.9/bin/mvn"
M2_REPO="$PROJECT_DIR/.m2"
JAR_GLOB="target/thingspace-order-server-*.jar"

export MAVEN_USER_HOME="$M2_REPO"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
ok()   { printf "${GREEN}✔ %s${NC}\n" "$*"; }
warn() { printf "${YELLOW}⚠ %s${NC}\n" "$*"; }
err()  { printf "${RED}✖ %s${NC}\n" "$*"; exit 1; }

# ---- Load .env (must exist for real deploys) ----
if [ ! -f .env ]; then
  err "No .env found. Create it: cp .env.example .env  (then fill real credentials)."
fi
set -a; source .env; set +a

# ---- Strict credential checks (no placeholders, no empty) ----
PLACEHOLDERS="REPLACE_ME your_client_id your_client_secret your_vz_m2m_token your_account_name placeholder"

require() {
  local name="$1" val="${!1:-}"
  if [ -z "$val" ]; then
    err "$name is empty — deploy aborted. Fill it in .env with REAL values."
  fi
  for w in $PLACEHOLDERS; do
    if [ "$val" = "$w" ]; then
      err "$name is still the placeholder '$val' — deploy aborted. Use real credentials."
    fi
  done
}

require DB_PASSWORD
require THINGSPACE_OAUTH_CLIENT_ID
require THINGSPACE_OAUTH_CLIENT_SECRET
require THINGSPACE_VZ_M2M_TOKEN
require THINGSPACE_ACCOUNT_NAME

# ---- Force production, no mock ----
export THINGSPACE_ENVIRONMENT=PRODUCTION
warn "THINGSPACE_ENVIRONMENT forced to PRODUCTION (mock mode is blocked)."
ok "Credentials OK: account=${THINGSPACE_ACCOUNT_NAME}"

# ---- Postgres reachability ----
if command -v pg_isready >/dev/null 2>&1; then
  if ! pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" >/dev/null 2>&1; then
    err "Postgres not reachable at $DB_HOST:$DB_PORT. Start it first (see DEPLOY.md)."
  fi
  ok "Postgres reachable at $DB_HOST:$DB_PORT"
fi

# ---- Maven present? ----
if [ ! -x "$MAVEN_BIN" ]; then
  err "Local Maven not found at $MAVEN_BIN — run ./deploy.sh build ONCE with internet first."
fi

# ---- Build (offline) ----
do_build() {
  ok "Building OFFLINE (using .m2 cache)..."
  "$MAVEN_BIN" -o -q -B clean package -DskipTests
  JAR=$(ls $JAR_GLOB 2>/dev/null | head -1)
  [ -n "$JAR" ] || err "Build succeeded but no jar found at $JAR_GLOB"
  ok "Built: $JAR ($(du -h "$JAR" | cut -f1))"
}

# ---- Run ----
do_run() {
  JAR=$(ls $JAR_GLOB 2>/dev/null | head -1)
  [ -n "$JAR" ] || err "No jar found — run './deploy-live.sh build' first."

  echo ""
  ok "Starting ThingSpace Order Server (PRODUCTION) on http://localhost:${PORT:-8080}"
  echo "   Endpoints:"
  echo "     http://localhost:${PORT:-8080}/api/orders"
  echo "     http://localhost:${PORT:-8080}/api/orders/check-status"
  echo "     http://localhost:${PORT:-8080}/api/orders/poll-now"
  echo "     http://localhost:${PORT:-8080}/api/device-orders"
  echo "     http://localhost:${PORT:-8080}/api/callbacks/device-service"
  echo ""
  exec java -jar "$JAR"
}

# ---- Main ----
ACTION="${1:-all}"
case "$ACTION" in
  build) do_build ;;
  run)   do_run ;;
  all)   do_build; do_run ;;
  *)     err "Usage: $0 [build|run|all]" ;;
esac