# ThingSpace Order Collector — Live Server

A small Spring Boot service that:
- Polls ThingSpace on a schedule for order status across your devices
- Stores results in Postgres (deduped on `orderRequestId` + `deviceId`)
- Exposes collected orders over a REST API

## Status

The app is **fully self-contained** — it no longer depends on the external
`verizon-apis-sdk` Maven artifact (that jar only ships the `com.verizon.m5gedge`
package, which doesn't match the `com.verizon.thingspace` API surface this code was
built against, and has no credential-management controller). Instead it carries a
small internal client (`com.verizon.thingspace.*`) that calls the exact endpoints
verified from the official SDK sources. See STATUS.md for details.

The app builds cleanly with Java 17 + Maven (`./deploy.sh`), verified locally.

Two paths are implemented:

- **`POST /api/orders/check-status`** — builds a `CheckOrderStatusRequest` (confirmed
  fields: `accountName`, optional `orderRequestId`, `devices`) and submits it to ThingSpace
  via `DeviceManagementController.uploadDeviceIdentifier(body)` → `POST /m2m/v1/devices/requests/status`
  → `ApiResponse<DeviceManagementResult>` (single field: `requestId`). The returned
  `requestId` is saved to the `order_records` table with status `PENDING` so the polling
  loop can track it once the async callback arrives. Auth: `thingspace_oauth` +
  `VZ-M2M-Token` (no session token needed).
- **`POST /api/callbacks/device-service`** — a webhook receiver. Per Verizon's docs,
  Check Order Status doesn't return status synchronously — it triggers an async callback
  to a URL you register separately. This endpoint captures whatever ThingSpace sends,
  storing the raw payload so nothing is lost while we figure out the exact field shape.
  **This URL must be publicly reachable** (e.g. `https://your-app.onrender.com/api/callbacks/device-service`)
  and registered with ThingSpace (likely via `ConnectivityCallbacksController` — not yet
  wired up) before callbacks will actually arrive.
- **`GET /api/callbacks`** — view everything received so far, useful for confirming the
  real payload shape once a callback lands, so we can replace raw-JSON storage with
  proper parsed fields.

For context on what `AccountRequestsController`'s single endpoint does: it only checks
the status of a request you already submitted elsewhere (e.g. an order or activation).
So the original polling server tracks a list of **open request IDs** you feed it, not
devices directly. `AsynchronousRequestResult` returns just `requestId` and `status` (a
`RequestStatusEnum`), both of which are stored per record.

One thing worth confirming yourself: `AccountRequestsController`'s `@withAuth` block only
requires `thingspace_oauth` and `VZ-M2M-Token` — not a session token — so this skeleton
never calls `SessionManagementController`. If you add endpoints elsewhere that do need a
session, you'll have to wire that up separately.

## Requirements

- Java 17+
- Maven
- A running Postgres instance
- ThingSpace credentials (OAuth client id/secret, VZ-M2M token, account name)

## Setup

> Full deployment instructions for Render, Docker, bare-metal, and Android
> (Termux/Acode) are in **[DEPLOY.md](DEPLOY.md)**.

1. Create a Postgres database:
   ```bash
   createdb thingspace_orders
   ```

2. Set environment variables (or put them in a `.env` file and export before running):
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=thingspace_orders
   export DB_USER=postgres
   export DB_PASSWORD=postgres

   export THINGSPACE_OAUTH_CLIENT_ID=your_client_id
   export THINGSPACE_OAUTH_CLIENT_SECRET=your_client_secret
   export THINGSPACE_VZ_M2M_TOKEN=your_vz_m2m_token
   export THINGSPACE_ACCOUNT_NAME=your_account_name
   export THINGSPACE_REQUEST_IDS=ae1b5618-7620-4197-ab54-bb1d13c561234,f55fea16-3664-4a32-ae9d-c0cbe3eedf1d
   export THINGSPACE_ENVIRONMENT=PRODUCTION
   export THINGSPACE_POLL_INTERVAL_MS=300000
   ```

3. Run it (any platform — see DEPLOY.md for all options):
   ```bash
   ./start.sh
   # or: mvn spring-boot:run
   # or: docker compose up
   ```

The server starts on `http://localhost:8080`. The scheduler kicks off immediately and then
repeats every `THINGSPACE_POLL_INTERVAL_MS` milliseconds (default: every 5 minutes).

## API

> There's also a **web dashboard** (single static page in `dashboard/`) you can
> deploy to Vercel or open from `file://`. See DEPLOY.md §5 and pass the API URL
> with `?api=https://your-api.example.com`.

| Method | Path                          | Description                                    |
|--------|-------------------------------|--------------------------------------------------|
| GET    | `/api/orders`                 | All collected request-status records              |
| GET    | `/api/orders/{requestId}`     | Record for a single request ID                    |
| POST   | `/api/orders/check-status`    | Submit a Check Order Status request (returns requestId) |
| POST   | `/api/orders/poll-now`        | Trigger an immediate poll cycle (for testing)      |
| GET    | `/api/device-orders`          | Current device-order manifest (line items)          |
| GET    | `/api/device-orders/summary`  | Totals, per-model counts, and add-on counts         |
| GET    | `/api/device-details`         | Per-device tracking (IMEI, phone numbers, transfers) |
| GET    | `/api/device-details/summary` | Transfer status summary                              |
| POST   | `/api/device-details`         | Add a new device detail                              |
| PUT    | `/api/device-details/{id}`    | Update a device detail                               |
| POST   | `/api/credentials/retrieve`   | Retrieve device credentials from ThingSpace          |
| POST   | `/api/credentials/generate`   | Generate device credentials                          |
| POST   | `/api/credentials/reset`      | Reset device credentials                             |
| POST   | `/api/credentials/drop`       | Drop device credentials                              |
| POST   | `/api/callbacks/device-service` | Webhook receiver for ThingSpace async callbacks  |
| GET    | `/api/callbacks`              | View raw callback payloads received                |

## Notes

- **Environment**: defaults to `PRODUCTION`. The `Environment` enum in the SDK only has
  `PRODUCTION` and `MOCK_SERVER_FOR_LIMITED_AVAILABILITY_SEE_QUICK_START` — there is no
  `STAGING`, `DEV`, or `QA` (verified against `Environment.java` in the SDK source).
- **Where do request IDs come from?** Request IDs are now created two ways: (1) the
  `POST /api/orders/check-status` endpoint submits a Check Order Status request and saves
  the returned requestId automatically, and (2) you can feed existing request IDs via
  the `THINGSPACE_REQUEST_IDS` env var for the polling loop to track.
- **Session token confirmed unnecessary for this endpoint**: `AccountRequestsController`'s
  auth block only requires `thingspace_oauth` + `VZ-M2M-Token`, not a session token — so this
  skeleton doesn't call `SessionManagementController` at all. If other endpoints you add later
  do need a session, you'll have to wire that up separately.
- **Migrations**: `ddl-auto: update` is fine for getting started but isn't safe for
  production schema changes — swap in Flyway or Liquibase before this goes live for real.
- **Rate limits**: the polling loop has no backoff/throttling between requests — add a
  delay or batch size limit if you're tracking a large number of request IDs.
