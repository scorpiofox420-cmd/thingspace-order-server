#!/usr/bin/env bash
#
# Start script for ThingSpace Order Server
# Works on: Linux, macOS, Windows (Git Bash), Android (Termux), Docker
#
set -e

echo "== ThingSpace Order Server =="

# ---- Detect environment ----
IS_DOCKER=false
IS_TERMUX=false
IS_MOBILE=false

if [ -f /.dockerenv ]; then
  IS_DOCKER=true
elif command -v termux-setup-storage >/dev/null 2>&1; then
  IS_TERMUX=true
  IS_MOBILE=true
elif [ -d "/data/user/0" ]; then
  IS_MOBILE=true
fi

echo "Environment: Docker=$IS_DOCKER, Termux=$IS_TERMUX, Mobile=$IS_MOBILE"

# ---- Load .env if present ----
if [ -f .env ]; then
  echo "Loading .env file..."
  set -a; source .env; set +a
fi

# ---- Check for required env vars (use defaults if not set) ----
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-thingspace_orders}"
DB_USER="${DB_USER:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-postgres}"
THINGSPACE_ENVIRONMENT="${THINGSPACE_ENVIRONMENT:-PRODUCTION}"

# ---- Verify credentials ----
if [ "$THINGSPACE_OAUTH_CLIENT_ID" = "REPLACE_ME" ] || [ -z "$THINGSPACE_OAUTH_CLIENT_ID" ]; then
  echo ""
  echo "WARNING: ThingSpace credentials not configured."
  echo "  Copy .env.example to .env and fill in your credentials."
  echo "  Or export them as environment variables."
  echo ""
  if [ "$IS_DOCKER" = false ]; then
    exit 1
  fi
fi

# ---- Check Java ----
if ! command -v java >/dev/null 2>&1; then
  echo "ERROR: Java not found. Install Java 17+ first."
  if [ "$IS_TERMUX" = true ]; then
    echo "  Try: pkg install openjdk-17"
  elif [ "$IS_MOBILE" = true ]; then
    echo "  Install a Java runtime for your Android device."
  fi
  exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ] 2>/dev/null; then
  echo "WARNING: Java $JAVA_VERSION detected, but 17+ is required."
fi

# ---- Check Maven ----
if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven not found. Trying to use wrapper..."
  if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
    MVN="./mvnw"
  else
    echo "ERROR: Maven not found. Install it or use Docker instead."
    echo "  Docker: docker-compose up"
    if [ "$IS_TERMUX" = true ]; then
      echo "  Termux: pkg install maven"
    fi
    exit 1
  fi
else
  MVN="mvn"
fi

# ---- Check Postgres (skip in Docker, assume it's in another container) ----
if [ "$IS_DOCKER" = false ]; then
  echo "Checking Postgres connection..."
  if command -v pg_isready >/dev/null 2>&1; then
    if ! pg_isready -h "$DB_HOST" -p "$DB_PORT" > /dev/null 2>&1; then
      echo "WARNING: Can't reach Postgres at $DB_HOST:$DB_PORT"
      echo "  Start Postgres or run: docker-compose up db"
    fi
  else
    echo "  (pg_isready not available, skipping Postgres check)"
  fi
fi

# ---- Build ----
echo ""
echo "Building..."
$MVN clean package -DskipTests -q

# ---- Run ----
echo ""
echo "Starting server on http://localhost:${SERVER_PORT:-10000}..."
echo ""
echo "API endpoints:"
echo "  http://localhost:${SERVER_PORT:-10000}/api/orders"
echo "  http://localhost:${SERVER_PORT:-10000}/api/device-orders"
echo "  http://localhost:${SERVER_PORT:-10000}/api/device-details"
echo "  http://localhost:${SERVER_PORT:-10000}/api/credentials"
echo "  http://localhost:${SERVER_PORT:-10000}/api/callbacks"
echo ""

java -jar target/thingspace-order-server-0.1.0.jar
