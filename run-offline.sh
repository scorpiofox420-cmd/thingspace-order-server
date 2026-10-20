#!/usr/bin/env bash
#
# run-offline.sh — run the ThingSpace Order Server from Acode with NO internet.
#
# Everything runs on-device, fully offline:
#   - Uses the pre-built jar in target/ (no Maven, no downloads)
#   - H2 in-memory DB (no Postgres needed, no network)
#   - Seeded sample orders/dashboard data
#   - Binds 0.0.0.0 so other devices on the same LAN/phone can open it
#
# The server is available at:
#   - On this phone:     http://localhost:10000
#   - Other devices:     http://<this-phone-ip>:10000
#
# NOTE: ThingSpace live polling needs internet. Offline, the API still
# serves orders/callbacks/device data saved on-device.
#
# Usage:
#   ./run-offline.sh          # start in background (this session)
#   ./run-offline.sh stop     # stop the running server
#   ./run-offline.sh logs     # tail server logs
#
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

JAR="target/thingspace-order-server-0.1.0.jar"
LOG="$PROJECT_DIR/.server.log"
PORT="${PORT:-10000}"

[ -f "$JAR" ] || { echo "✖ No jar found at $JAR — run ./deploy.sh build once (needs internet)."; exit 1; }

start() {
  if pgrep -f thingspace-order-server >/dev/null 2>&1; then
    echo "✔ Server already running: http://localhost:$PORT"
    return
  fi
  echo "Starting ThingSpace Order Server (offline, H2) on http://0.0.0.0:$PORT ..."
  setsid nohup java -jar "$JAR" \
    --spring.profiles.active=h2 \
    --server.port="$PORT" \
    --server.address=0.0.0.0 > "$LOG" 2>&1 &
  echo "$!" > .server.pid
  sleep 1
  echo "✔ Started (pid $(cat .server.pid)). Log: $LOG"
  echo "  Local:   http://localhost:$PORT"
  echo "  LAN:     http://<this-phone-ip>:$PORT"
}

stop() {
  if pgrep -f thingspace-order-server >/dev/null 2>&1; then
    pkill -f thingspace-order-server
    echo "✔ Stopped."
  else
    echo "Not running."
  fi
}

case "${1:-start}" in
  start) start ;;
  stop)  stop ;;
  logs)  tail -f "$LOG" ;;
  *)     echo "Usage: $0 [start|stop|logs]" ;;
esac 