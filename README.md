# ThingSpace Order Collector — Live Server

A small Spring Boot service that:
- Polls ThingSpace on a schedule for order status across your devices
- Stores results in Postgres (deduped on `orderRequestId` + `deviceId`)
- Exposes collected orders over a REST API

## Status

Every part of the polling path is confirmed against real SDK source you shared —
`VerizonClient.Builder`, `AccountRequestsController`, and `AsynchronousRequestResult` —
so there are no remaining placeholder/TODO calls in that path.

A second path was added for **submitting** a Check Order Status request (as opposed to
polling for an already-known request's status):

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

3. Run it:
   ```bash
   mvn spring-boot:run
   ```

The server starts on `http://localhost:8080`. The scheduler kicks off immediately and then
repeats every `THINGSPACE_POLL_INTERVAL_MS` milliseconds (default: every 5 minutes).

## API

| Method | Path                          | Description                                    |
|--------|-------------------------------|--------------------------------------------------|
| GET    | `/api/orders`                 | All collected request-status records              |
| GET    | `/api/orders/{requestId}`     | Record for a single request ID                    |
| POST   | `/api/orders/check-status`    | Submit a Check Order Status request (returns requestId) |
| POST   | `/api/orders/poll-now`        | Trigger an immediate poll cycle (for testing)      |
| GET    | `/api/device-orders`          | Current device-order manifest (line items)          |
| GET    | `/api/device-orders/summary`  | Totals, per-model counts, and add-on counts         |
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
