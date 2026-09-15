#!/usr/bin/env bash
#
# deploy.sh — build and run the ThingSpace Order Server locally.
#
# Works anywhere with Java 17 (Termux, Acode, macOS, Linux, Git Bash,
# Docker, etc). If Maven isn't installed it downloads it automatically
# into .deploy-tools/ inside the project. All Maven cache lives in the
# project (.m2/) so nothing pollutes your home directory.
#
# Usage:
#   ./deploy.sh           # build + run
#   ./deploy.sh build     # build only (no run)
#   ./deploy.sh run       # run only (skip build)
#   ./deploy.sh clean     # wipe build artifacts + Maven cache
#
set -euo pipefail

# ---- Project root (where pom.xml lives) ----
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

# ---- Tool directories ----
DEPLOY_TOOLS="$PROJECT_DIR/.deploy-tools"
MAVEN_HOME_DIR="$DEPLOY_TOOLS/apache-maven-3.9.9"
MAVEN_BIN="$MAVEN_HOME_DIR/bin/mvn"
M2_REPO="$PROJECT_DIR/.m2"
JAR_GLOB="target/thingspace-order-server-*.jar"

export MAVEN_USER_HOME="$M2_REPO"

# ---- Helpers ----
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
ok()   { printf "${GREEN}✔ %s${NC}\n" "$*"; }
warn() { printf "${YELLOW}⚠ %s${NC}\n" "$*"; }
err()  { printf "${RED}✖ %s${NC}\n" "$*"; exit 1; }

# ---- Detect Java ----
find_java() {
  if command -v java >/dev/null 2>&1; then
    JAVA_CMD=java
  elif [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
  else
    err "Java 17 not found. Install OpenJDK 17 (pkg install openjdk-17 / apt install openjdk-17-jdk / brew install openjdk@17)."
  fi

  VER=$("$JAVA_CMD" -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
  [ "${VER:-0}" -ge 17 ] || err "Java $VER detected; 17+ required."
  ok "Java $VER  ($JAVA_CMD)"
}

# ---- Ensure Maven is available ----
ensure_maven() {
  if command -v mvn >/dev/null 2>&1; then
    MVN_CMD=mvn
    ok "Maven found on PATH: $(command -v mvn)"
    return
  fi

  if [ -x "$MAVEN_BIN" ]; then
    MVN_CMD="$MAVEN_BIN"
    ok "Using local Maven: $MAVEN_BIN"
    return
  fi

  warn "Maven not found — downloading to $MAVEN_HOME_DIR ..."
  mkdir -p "$DEPLOY_TOOLS"
  MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz"
  if command -v wget >/dev/null 2>&1; then
    wget -q "$MAVEN_URL" -O "$DEPLOY_TOOLS/maven.tar.gz"
  elif command -v curl >/dev/null 2>&1; then
    curl -sL "$MAVEN_URL" -o "$DEPLOY_TOOLS/maven.tar.gz"
  else
    err "Neither wget nor curl available — cannot download Maven."
  fi

  tar xzf "$DEPLOY_TOOLS/maven.tar.gz" -C "$DEPLOY_TOOLS"
  rm -f "$DEPLOY_TOOLS/maven.tar.gz"
  chmod +x "$MAVEN_BIN"

  if [ ! -x "$MAVEN_BIN" ]; then
    err "Maven download/extraction failed."
  fi

  MVN_CMD="$MAVEN_BIN"
  ok "Maven installed: $MAVEN_BIN"
}

# ---- Build ----
do_build() {
  echo ""
  ok "Building (M2 cache: $M2_REPO)..."
  "$MVN_CMD" -q -B clean package -DskipTests
  JAR=$(ls $JAR_GLOB 2>/dev/null | head -1)
  [ -n "$JAR" ] || err "Build succeeded but no jar found at $JAR_GLOB"
  ok "Built: $JAR ($(du -h "$JAR" | cut -f1))"
}

# ---- Run ----
do_run() {
  JAR=$(ls $JAR_GLOB 2>/dev/null | head -1)
  [ -n "$JAR" ] || err "No jar found at $JAR_GLOB — run './deploy.sh build' first."

  # Load .env if present
  [ -f .env ] && { set -a; source .env; set +a; }

  PORT="${PORT:-${SERVER_PORT:-8080}}"
  echo ""
  ok "Starting ThingSpace Order Server on http://localhost:$PORT"
  echo "   Endpoints:"
  echo "     http://localhost:$PORT/api/orders"
  echo "     http://localhost:$PORT/api/device-orders"
  echo "     http://localhost:$PORT/api/device-details"
  echo "     http://localhost:$PORT/api/credentials"
  echo "     http://localhost:$PORT/api/callbacks"
  echo "   Dashboard: open dashboard/index.html or"
  echo "     http://localhost:$PORT/?api=http://localhost:$PORT"
  echo ""

  exec "$JAVA_CMD" -jar "$JAR"
}

# ---- Clean ----
do_clean() {
  ok "Cleaning build artifacts..."
  "$MVN_CMD" -q clean 2>/dev/null || true
  rm -rf "$PROJECT_DIR/target" "$M2_REPO"
  ok "Clean."
}

# ---- Main ----
ACTION="${1:-all}"

find_java
ensure_maven
mkdir -p "$M2_REPO"

case "$ACTION" in
  build) do_build ;;
  run)   do_run ;;
  clean) do_clean ;;
  all)   do_build; do_run ;;
  *)     err "Usage: $0 [build|run|clean|all]" ;;
esac
