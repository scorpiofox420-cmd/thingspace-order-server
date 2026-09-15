# Where To Go — Exact Webpages You Need

## 1. Get your ThingSpace API credentials

You need two different things, and Verizon's docs are clear about where to get each:

**A. ThingSpace API token (OAuth key + secret)**
1. Go to **https://thingspace.verizon.com** and sign in (or sign up if you don't have an account).
2. Click your name/icon in the top right corner.
3. Go to **Account settings → Key Management** (also reachable via "My Keys" on the home page).
4. Copy the **key** and **secret** shown there.
   → These map to `THINGSPACE_OAUTH_CLIENT_ID` and `THINGSPACE_OAUTH_CLIENT_SECRET` in the server's config.

**B. UWS credentials (for the VZ-M2M / Connectivity Management side)**
- If your company is an existing Verizon Enterprise customer, you likely already have a
  **UWS (Unified Web Services) account** — these are shared "machine" credentials for
  your whole company, not per-developer.
- Log in to **ThingSpace Manage** at `thingspace.verizonwireless.com/Portal/manage/dashboard`
  → click **Admin** in the navigation bar to find/manage these.
- If you don't see a UWS user there, contact your Verizon sales/account representative to
  request one — this isn't self-serve if your company doesn't already have it set up.
- These credentials are what you exchange for a `VZ-M2M` session/token
  (`THINGSPACE_VZ_M2M_TOKEN` in the server's config).

## 2. Deploy the server

1. **GitHub** — `https://github.com` — create a free account if you don't have one, then
   create a new repository and push the `thingspace-order-server` folder to it.
2. **Render** — `https://render.com` — sign up free, click **New +** → **Web Service**,
   connect the GitHub repo you just created. (Full step-by-step was covered earlier in
   this conversation — ask me to repeat it if you need it again.)
3. Also on Render: **New +** → **PostgreSQL** to get a free database instance.

## 3. Once deployed

Your live app will be at a URL like:
```
https://your-app-name.onrender.com
```

Useful pages once it's running:
- `https://your-app-name.onrender.com/api/orders` — view collected order/request status
- `https://your-app-name.onrender.com/api/callbacks` — view raw callback payloads received

## Still open

The callback URL (`.../api/callbacks/device-service`) needs to be **registered with
ThingSpace** before it'll actually receive anything — that's a separate step (likely
through ThingSpace's callback registration settings, tied to `ConnectivityCallbacksController`
in the SDK) that hasn't been built yet. Once you're deployed and have a real URL, let me
know and we can find exactly where in the ThingSpace portal that registration happens.
