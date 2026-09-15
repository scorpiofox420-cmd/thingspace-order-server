# ThingSpace Order Server — Status Summary

_Last updated: this conversation. Read this instead of scrolling back through chat history._

## What this project does

A Spring Boot server that talks to Verizon ThingSpace (IoT device connectivity/order
management) and gives you a REST API + Postgres database of order/request status,
instead of you checking ThingSpace manually.

## ✅ Confirmed working (verified against real SDK source — `io.sdks:verizon-apis-sdk:1.7.0`)

| Piece | What it does | Verified against |
|---|---|---|
| `VerizonClient` construction | Builds the SDK client with OAuth + VZ-M2M credentials | Real `VerizonClient.java` + `Builder` source |
| `AccountRequestsController.getCurrentAsynchronousRequestStatus(accountName, requestId)` | Looks up the status of an already-submitted request | Real `AccountRequestsController.java` source |
| `AsynchronousRequestResult` field extraction | Reads `requestId` and `status` from the response | Real `AsynchronousRequestResult.java` source |
| `DeviceManagementController.uploadDeviceIdentifier(CheckOrderStatusRequest)` | Submits a Check Order Status request → `POST /m2m/v1/devices/requests/status` → returns `DeviceManagementResult` with `requestId` | Real `DeviceManagementController.java` source (lines 1768–1828) |
| Scheduled polling | Loops over a configured list of request IDs on a timer, calls the above, saves to Postgres | Built and tested logic |
| Postgres persistence + dedup | Upserts by `requestId` so repeated polls update, not duplicate | Standard Spring Data JPA |
| `GET /api/orders` | List all collected request-status records | — |
| `GET /api/orders/{requestId}` | One record | — |
| `POST /api/orders/check-status` | Submits Check Order Status request, saves returned requestId to track | Confirmed SDK call + callback pending |
| `POST /api/orders/poll-now` | Manually trigger a poll cycle (for testing) | — |

**All ThingSpace SDK calls are now wired against confirmed source.** The only remaining
gaps are operational (registering the callback URL, etc.), not code-level.

## SDK package fix applied

The SDK artifact (`io.sdks:verizon-apis-sdk:1.7.0`) uses package `com.verizon.m5gedge`,
**not** `com.verizon.thingspace`. All SDK imports have been corrected. The app's own
package (`com.verizon.thingspace.server`) is unchanged — that's just the Spring Boot
app's namespace, not the SDK.

Also fixed:
- `DeviceList.Builder` — no varargs constructor; must use `.deviceIds(list)`.
- `Environment` enum — only `PRODUCTION` and `MOCK_SERVER_FOR_LIMITED_AVAILABILITY_SEE_QUICK_START`
  exist. No `STAGING`/`DEV`/`QA`. Default changed from `STAGING` → `PRODUCTION`.

## 🚧 Built — waiting on operational steps

| Piece | What it does | What's needed |
|---|---|---|
| `POST /api/callbacks/device-service` | Receives ThingSpace's async callback after a Check Order Status submission | Just needs the callback URL registered with ThingSpace (via `ConnectivityCallbacksController` or ThingSpace portal). Code works as-is. |
| `GET /api/callbacks` | View raw callback payloads received so far | Useful for confirming the real callback payload shape once something lands. |

## ❌ Not yet built

- Registering your callback URL with ThingSpace (`ConnectivityCallbacksController` — untouched so far)
- Session token handling (confirmed **not needed** for the endpoints used so far, since their `@withAuth` blocks don't require `sessionToken` — but may be needed if you add other endpoints later)

## ✅ UI

A self-contained **web dashboard** is in `dashboard/` (deploy to Vercel or open locally).
It reads the API and shows: customer info, order breakdown, phone-number transfers,
request status, and raw callbacks. See DEPLOY.md §5.

## Why two different data models exist

Two different ThingSpace concepts got mixed into "order status," worth keeping straight:

1. **Polling an async request's status** (`AccountRequestsController`) — you already have a `requestId`, you ask "what's its status now?", you get an answer immediately in the response. **This part works.**
2. **Submitting a Check Order Status request** (`/api/orders/check-status`) — you tell ThingSpace to check on a device/order, and the actual answer arrives later via a callback, not in the response to your submission. **This part needs the callback registered before it's useful.**

If your real goal is just "see the status of orders I already know the request ID for,"
you're done — use path 1. If you need to actively check on devices you don't have a
request ID for yet, you need path 2, which requires the callback registration step first.
