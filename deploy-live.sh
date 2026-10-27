# ThingSpace Order Server — WORK RESUME

> Handoff file. Everything needed to continue from a fresh session.
> Conversation transcript: `thingspace-conversation.txt` (same folder).

## Goal
Finish deploying the **ThingSpace Order Server** and complete the real Verizon
ThingSpace order for account `742899750-00001`.

## Current state (verified)
- **Render service is LIVE and healthy.**
  - URL: `https://thingspace-order-server-tkv4.onrender.com` <br>
  - Service ID: `srv-db1gprfavr4c73brfilg` (Render account slug `thingspace-order-server-tkv4`)
  - Latest deploy: `dep-db40p80473hc73c7r2v0` → status `live` (trigger API, Oct 8 21:37 UTC)
  - Health probe: `GET /api/orders` → `200 []` (app up, **no orders recorded yet**)
  - Runtime: Docker, free plan, linked to `github.com/scorpiofox420-cmd/thingspace-order-server`
    branch `master`, `autoDeploy: yes`
- **Real ThingSpace credentials ARE configured on Render** env vars:
  - `THINGSPACE_OAUTH_CLIENT_ID` = `ac1271c5-ef51-4299-82aa-479bb5afd6c3`
  - `THINGSPACE_OAUTH_CLIENT_SECRET` = `f2f26495-05fa-4d99-9383-3c18e2a307db`
  - `THINGSPACE_VZ_M2M_TOKEN` = `M2U0NTE0NzMtYWMyZC00M2JhLWFiNGEtMTUyMDM5ZDc0NmIw`
  - `THINGSPACE_ACCOUNT_NAME` = `742899750-00001`
  - `THINGSPACE_BASE_URL` / `THINGSPACE_CREDENTIAL_BASE_URL` = `https://thingspace.verizon.com/api`

## Blocker (NOT resolved)
- The GitHub repo `scorpiofox420-cmd/thingspace-order-server` is **private**.
- PAT provided in-conversation authenticates but GitHub denies it on that repo:
  `git ls-remote` → `403 Write access to repository not granted`, API → `404`.
  It only covers the 36 **public** repos.
- User said "i grant" but it still returns 403/404 (token was not re-verified after granting).
- **Needed to proceed:** re-grant the fine-grained token
  (`Contents: Read and write` on that repo), make repo public, or paste a
  classic PAT with `repo` scope. Then re-test:
  ```
  git ls-remote https://x-access-token:$(cat /tmp/ghkey)@github.com/scorpiofox420-cmd/thingspace-order-server.git
  ```

## Keys / credentials (where they live)
- **GitHub PAT** → `github.com/settings/tokens`, pasted in this conversation
  (see `thingspace-conversation.txt`; also extractable via:
  `grep -aohE 'github_pat_[A-Za-z0-9_]{20,}' /public/.local/state/opencode/prompt-history.jsonl | head -1`).
  Cached at `/tmp/ghkey`.
- **Render API key** → `/tmp/rk` = `rnd_8ac2nWbmMm5jf0U4qkBNpNAnCKZG`.
  Helper: `/tmp/rput.js <svc> <METHOD> <path> [bodyfile]` (Authorization: Bearer).

  (in conversation; project `thingspace-order-server-server` had a failing
  `parcel: command not found` build — the dashboard deploy, not the Java API).

## What still needs doing
1. Get real GitHub write access (blocker above).
2. Push the intended repo state; Render auto-deploys on push.
3. Set real `THINGSPACE_REQUEST_IDS` (currently the literal string `fromEnv`),
   plus `THINGSPACE_DEVICE_MDN/IMEI/ICCID` if the app needs them — retrieve from
   ThingSpace, e.g. via `POST /api/orders/check-status` flow.
4. Confirm a real order appears: `GET https://thingspace-order-server-tkv4.onrender.com/api/orders`.
5. (Optional) Vercel dashboard fix — repo `thingspace-order-server` build script
   runs `parcel build` but package.json lacks a devDependency on parcel.

## Repo clones on this machine
- `/tmp/tsapp` — loose ThingSpace app files (controllers, Dockerfile, start.sh,
  render.yaml, deploy-live.sh, index/order-live dashboards). No `pom.xml`.
- `/tmp/tsrepo` — unrelated opencode-on-render template.
- Local git repos are scratch copies; source of truth is the private GitHub repo.

## How to resume this session
- opencode stores sessions in `/public/.local/share/opencode/opencode.db`.
- Session: `ses_ee27d0a04ffea9…` (this conversation).
- Reopen via the opencode session picker, or run `opencode` and select it.
- Raw transcript exported to `/public/thingspace-conversation.txt`.
