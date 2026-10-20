# Deployment Guide

The ThingSpace Order Server is a Spring Boot (Java 17) app. Here's how to
run it on every platform.

## Quick Pick

| Where | How | Notes |
|-------|-----|-------|
| **Render (recommended)** | Push to GitHub → Render Blueprint | Free tier, managed Postgres, one-click |
| **Docker (any machine)** | `docker-compose up` | Local dev with Postgres container |
| **Bare-metal (Linux/macOS/Windows+Git Bash)** | `./start.sh` | Needs Java 17 + Maven + Postgres |
| **Android (Acode / Termux)** | `./start.sh` | Install Java + Maven via Termux |
| **Vercel** | Not supported for Java | Use a static dashboard instead (below) |

---

## 1. Render (recommended — production)

Already configured via `render.yaml`. See the **Deploy steps** section in
README.md. Push to GitHub, then:

1. Render.com → **New +** → **Blueprint**
2. Select this repo — it reads `render.yaml`
3. Fill in the ThingSpace secrets it prompts for
4. Apply. Render creates Postgres + the web service automatically.

Callback URL to register with ThingSpace:
```
https://your-app.onrender.com/api/callbacks/device-service
```

## 2. Docker Compose (local development)

```bash
docker compose up
```

- Starts Postgres (port 5432) + the app (port 10000)
- Data persists in a named volume
- ThingSpace credentials come from `.env` or shell env vars
- Stop with `docker compose down`

Useful variations:
```bash
docker compose up db       # Postgres only
docker compose up app      # app only (needs db running)
```

## 3. Bare-metal (any terminal)

Requirements: **Java 17**, **Maven**, **Postgres 16**

```bash
# 1. Set credentials in .env (copy from .env.example)
cp .env.example .env
# ...edit .env with real ThingSpace values...

# 2. Start Postgres (or use an existing one)
createdb thingspace_orders

# 3. Run
./start.sh
```

`start.sh` auto-detects the platform, checks tools, builds, and runs.
Server: `http://localhost:10000`.

## 4. Android — Acode / Termux

The app runs natively on Android via Termux:

```bash
# Install Java 17 and Maven
pkg update
pkg install openjdk-17 maven postgresql

# Start Postgres
mkdir -p $PREFIX/var/lib/postgresql
initdb $PREFIX/var/lib/postgresql/data
pg_ctl -D $PREFIX/var/lib/postgresql/data -l pg.log start
createdb thingspace_orders

# Set credentials, then run
cp .env.example .env
# ...edit .env...
./start.sh
```

Server: `http://localhost:10000`. On your phone, open `http://192.168.x.x:8080`
from other devices on the same Wi-Fi.

## 5. Vercel (web dashboard)

**Java/Spring Boot cannot run on Vercel** (it's a Node.js/serverless platform), but the
**web dashboard** that reads the API *can*. It's a single static HTML file at
`dashboard/`.

**Deploy:**

1. Keep the API running on Render (section 1) or localhost.
2. On vercel.com, create a project and set **Root Directory** to `dashboard`
   (this repo contains a `dashboard/vercel.json` that makes Vercel serve it as-is).
3. Deploy. You get a URL like `https://your-dashboard.vercel.app`.
4. Open the dashboard with the API base set:
   ```
   https://your-dashboard.vercel.app/?api=https://your-app.onrender.com
   ```
   (or type the API URL into the box in the dashboard header — it's saved for the session).

**CORS:** the API server allows browser calls from any origin by default
(`CORS_ALLOWED_ORIGINS=*`, configured in `application.yml`). For production,
lock it down:
```
CORS_ALLOWED_ORIGINS=https://your-dashboard.vercel.app
```

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | Postgres host |
| `DB_PORT` | `5432` | Postgres port |
| `DB_NAME` | `thingspace_orders` | Postgres database |
| `DB_USER` | `postgres` | Postgres user |
| `DB_PASSWORD` | `postgres` | Postgres password |
| `DB_PARAMS` | *(empty)* | Extra JDBC params, e.g. `?sslmode=require` (Render) |
| `SERVER_PORT` / `PORT` | `10000` | HTTP port |
| `THINGSPACE_OAUTH_CLIENT_ID` | — | ThingSpace OAuth key |
| `THINGSPACE_OAUTH_CLIENT_SECRET` | — | ThingSpace OAuth secret |
| `THINGSPACE_VZ_M2M_TOKEN` | — | VZ-M2M session token |
| `THINGSPACE_ACCOUNT_NAME` | — | Billing account name |
| `THINGSPACE_REQUEST_IDS` | — | Comma-separated request UUIDs to poll |
| `THINGSPACE_ENVIRONMENT` | `PRODUCTION` | SDK environment |
| `THINGSPACE_POLL_INTERVAL_MS` | `300000` | Poll interval (ms) |
| `THINGSPACE_BASE_URL` | *(empty)* | Override the API base URL |
| `THINGSPACE_CREDENTIAL_BASE_URL` | *(empty)* | Override the credential-management base URL |
| `CORS_ALLOWED_ORIGINS` | `*` | Comma-separated browser origins allowed to call `/api/**` |
| `CORS_ALLOWED_METHODS` | `*` | Comma-separated CORS methods (e.g. `GET,POST,PUT`) |