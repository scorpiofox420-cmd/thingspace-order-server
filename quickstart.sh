#!/usr/bin/env bash
#
# Quickstart: build and run the ThingSpace order server locally.
# Run this from inside the thingspace-order-server/ folder.
#
# Before running: fill in the export lines below with your real values,
# or set these as actual environment variables / a .env file instead.

set -e

echo "== ThingSpace Order Server — Quickstart =="

# ---- 1. Fill these in --------------------------------------------------
export DB_HOST="localhost"
export DB_PORT="5432"
export DB_NAME="thingspace_orders"
export DB_USER="postgres"
export DB_PASSWORD="postgres"

export THINGSPACE_OAUTH_CLIENT_ID="REPLACE_ME"
export THINGSPACE_OAUTH_CLIENT_SECRET="REPLACE_ME"
export THINGSPACE_VZ_M2M_TOKEN="REPLACE_ME"
export THINGSPACE_ACCOUNT_NAME="REPLACE_ME"          # e.g. 0251687987-0001
export THINGSPACE_REQUEST_IDS="REPLACE_ME"            # comma-separated UUIDs
export THINGSPACE_ENVIRONMENT="PRODUCTION"            # or STAGING/DEV/QA — see README
export THINGSPACE_POLL_INTERVAL_MS="300000"           # 5 minutes

# ---- 2. Check for placeholder values still in place ---------------------
if [[ "$THINGSPACE_OAUTH_CLIENT_ID" == "REPLACE_ME" ]]; then
  echo ""
  echo "⚠️  You haven't filled in your real credentials yet."
  echo "    Edit this script (quickstart.sh) and replace the REPLACE_ME values"
  echo "    with your actual ThingSpace credentials before running again."
  echo ""
  exit 1
fi

# ---- 3. Make sure Postgres is reachable ---------------------------------
echo "Checking Postgres connection..."
if ! pg_isready -h "$DB_HOST" -p "$DB_PORT" > /dev/null 2>&1; then
  echo "⚠️  Can't reach Postgres at $DB_HOST:$DB_PORT."
  echo "    Start Postgres first, or update DB_HOST/DB_PORT above."
  exit 1
fi
echo "✅ Postgres is reachable."

# ---- 4. Build ------------------------------------------------------------
echo "Building..."
mvn clean package -DskipTests

# ---- 5. Run ---------------------------------------------------------------
echo ""
echo "Starting the server on http://localhost:8080 ..."
echo "Try these once it's up:"
echo "  curl http://localhost:8080/api/orders"
echo "  curl -X POST http://localhost:8080/api/orders/poll-now"
echo ""
java -jar target/thingspace-order-server-0.1.0.jar
