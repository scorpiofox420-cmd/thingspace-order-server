package com.verizon.thingspace;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.verizon.thingspace.controllers.AccountRequestsController;
import com.verizon.thingspace.controllers.DeviceCredentialManagementController;
import com.verizon.thingspace.controllers.DeviceManagementController;
import com.verizon.thingspace.exceptions.ErrorResponseException;
import com.verizon.thingspace.models.OauthToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Self-contained ThingSpace REST client.
 *
 * This replaces the external verizon-apis-sdk dependency (which only ships the
 * com.verizon.m5gedge package) with the com.verizon.thingspace API surface the
 * server code was written against, calling the exact endpoints verified from
 * the official SDK sources:
 *
 *   - GET  {base}/m2m/v1/accounts/{aname}/requests/{requestId}/status
 *   - POST {base}/m2m/v1/devices/requests/status
 *   - OAuth token: POST {base}/ts/v1/oauth2/token (grant_type=client_credentials)
 *   - VZ-M2M session: POST {base}/m2m/v1/session/login
 *
 * Auth headers sent on every call: Authorization Bearer (OAuth, when the
 * client id/secret are configured) and VZ-M2M-Token.
 */
public class VerizonClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final String baseUrl;
    private final String credentialBaseUrl;
    private final String oauthClientId;
    private final String oauthClientSecret;
    private final String vzM2mToken;
    private final String uwsUsername;
    private final String uwsPassword;
    private final long oauthClockSkew;

    private volatile OauthToken oauthToken;
    private volatile String sessionToken;
    private volatile long sessionTokenExpiresAt;

    public VerizonClient(String baseUrl, String credentialBaseUrl,
                         String oauthClientId, String oauthClientSecret, String vzM2mToken,
                         long oauthClockSkew) {
        this(baseUrl, credentialBaseUrl, oauthClientId, oauthClientSecret, vzM2mToken,
                null, null, oauthClockSkew);
    }

    /**
     * @param uwsUsername, uwsPassword optional UWS credentials. When both are present and no
     *     {@code vzM2mToken} is configured, the client generates the VZ-M2M session token
     *     programmatically via POST /m2m/v1/session/login and keeps it refreshed while in use.
     */
    public VerizonClient(String baseUrl, String credentialBaseUrl,
                         String oauthClientId, String oauthClientSecret, String vzM2mToken,
                         String uwsUsername, String uwsPassword, long oauthClockSkew) {
        this.baseUrl = trimSlash(baseUrl);
        this.credentialBaseUrl = trimSlash(credentialBaseUrl);
        this.oauthClientId = oauthClientId;
        this.oauthClientSecret = oauthClientSecret;
        this.vzM2mToken = vzM2mToken;
        this.uwsUsername = uwsUsername;
        this.uwsPassword = uwsPassword;
        this.oauthClockSkew = oauthClockSkew > 0 ? oauthClockSkew : 60L;
    }

    public AccountRequestsController getAccountRequestsController() {
        return new AccountRequestsController(this);
    }

    public DeviceManagementController getDeviceManagementController() {
        return new DeviceManagementController(this);
    }

    public DeviceCredentialManagementController getDeviceCredentialManagementController() {
        return new DeviceCredentialManagementController(this);
    }

    public String baseUrl() { return baseUrl; }
    public String credentialBaseUrl() { return credentialBaseUrl; }
    public ObjectMapper mapper() { return MAPPER; }

    /** Immutable result of one HTTP request. */
    public static class HttpResult {
        private final int statusCode;
        private final String body;

        HttpResult(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body == null ? "" : body;
        }

        public int statusCode() { return statusCode; }
        public String body() { return body; }

        public JsonNode json() {
            if (body.isEmpty()) return MAPPER.nullNode();
            try {
                return MAPPER.readTree(body);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to parse response JSON", e);
            }
        }

        public void requireOk() {
            if (statusCode >= 300) {
                throw new ErrorResponseException(statusCode, body);
            }
        }
    }

    /**
     * Performs an authenticated request to the Verizon API.
     *
     * @param method "GET" or "POST"
     * @param url    full endpoint URL
     * @param jsonBody body object to serialize, or null
     */
    public HttpResult request(String method, String url, Object jsonBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60));

        String token = bearerToken();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        String m2m = sessionHeaderValue();
        if (m2m != null && !m2m.isEmpty()) {
            builder.header("VZ-M2M-Token", m2m);
        }

        if ("POST".equalsIgnoreCase(method)) {
            builder.header("Content-Type", "application/json");
            if (jsonBody != null) {
                try {
                    builder.method("POST", HttpRequest.BodyPublishers.ofString(
                            MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to serialize request body", e);
                }
            } else {
                builder.method("POST", HttpRequest.BodyPublishers.noBody());
            }
        } else {
            builder.method("GET", HttpRequest.BodyPublishers.noBody());
        }

        try {
            HttpResponse<String> response = http.send(builder.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new HttpResult(response.statusCode(), response.body());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Request to Verizon API failed: " + e.getMessage(), e);
        }
    }

    private String bearerToken() {
        OauthToken current = oauthToken;
        if (current != null && !current.isExpired(oauthClockSkew)) {
            return current.getAccessToken();
        }
        synchronized (this) {
            current = oauthToken;
            if (current != null && !current.isExpired(oauthClockSkew)) {
                return current.getAccessToken();
            }
            if (oauthClientId == null || oauthClientSecret == null
                    || oauthClientId.isEmpty() || oauthClientSecret.isEmpty()) {
                return null;
            }
            oauthToken = fetchToken();
            return oauthToken.getAccessToken();
        }
    }

    /** Fetches a fresh OAuth token via the Client Credentials grant. */
    private OauthToken fetchToken() {
        String credentials = oauthClientId + ":" + oauthClientSecret;
        String basic = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/ts/v1/oauth2/token"))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials",
                        StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = http.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 300) {
                throw new ErrorResponseException(response.statusCode(), response.body());
            }
            JsonNode node = MAPPER.readTree(response.body());
            String token = node.path("access_token").asText(null);
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("OAuth response missing access_token");
            }
            OauthToken oauth = MAPPER.treeToValue(node, OauthToken.class);
            if (oauth.getExpiry() == null || oauth.getExpiry() <= 0) {
                oauth.setExpiry(oauth.getExpiryEpochSec());
            }
            return oauth;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("OAuth token request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Resolves the VZ-M2M session token: uses the statically configured one
     * when present, otherwise generates/refreshes it from UWS credentials.
     * A generated session token expires after 20 minutes of inactivity.
     */
    private String sessionHeaderValue() {
        if (vzM2mToken != null && !vzM2mToken.isEmpty()) {
            return vzM2mToken;
        }
        if (uwsUsername == null || uwsPassword == null
                || uwsUsername.isEmpty() || uwsPassword.isEmpty()) {
            return null;
        }
        String current = sessionToken;
        if (current != null
                && System.currentTimeMillis() < sessionTokenExpiresAt - (oauthClockSkew * 1000L)) {
            return current;
        }
        synchronized (this) {
            current = sessionToken;
            if (current != null
                    && System.currentTimeMillis() < sessionTokenExpiresAt - (oauthClockSkew * 1000L)) {
                return current;
            }
            sessionToken = startSession();
            return sessionToken;
        }
    }

    /**
     * Starts a Connectivity Management session and returns the VZ-M2M session token.
     *
     *   POST {base}/m2m/v1/session/login
     *   Authorization: Bearer &lt;ThingSpace OAuth token&gt;
     *   Body:          {"username": "...", "password": "..."}
     *   Response:      {"sessionToken": "..."}
     */
    private String startSession() {
        String bearer = bearerToken();
        if (bearer == null) {
            throw new IllegalStateException(
                    "Cannot start VZ-M2M session: no OAuth token / client id+secret configured");
        }
        String body = "{\"username\":\"" + escape(uwsUsername)
                + "\",\"password\":\"" + escape(uwsPassword) + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/m2m/v1/session/login"))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + bearer)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = http.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 300) {
                throw new ErrorResponseException(response.statusCode(), response.body());
            }
            JsonNode node = MAPPER.readTree(response.body());
            String token = node.path("sessionToken").asText(null);
            if (token == null || token.isEmpty()) {
                node = node.path("data");   // some session responses nest the token
                token = node.path("sessionToken").asText(null);
            }
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException(
                        "Session login response missing sessionToken: " + response.body());
            }
            sessionTokenExpiresAt = System.currentTimeMillis() + 20L * 60L * 1000L;
            return token;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("VZ-M2M session login failed: " + e.getMessage(), e);
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** Current VZ-M2M session token (static or generated) for tests / debugging. */
    public String sessionTokenForDebug() {
        return sessionHeaderValue();
    }

    /** Bearer token for tests / debugging (no network). */
    public String oauthTokenForDebug() {
        OauthToken current = oauthToken;
        return current == null ? null : current.getAccessToken();
    }

    public static String trimSlash(String url) {
        if (url == null || url.isEmpty()) return "";
        return url.replaceAll("/+$", "");
    }
}
