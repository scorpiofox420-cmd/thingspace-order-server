package com.verizon.thingspace.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Bound from application.yml, which in turn reads from environment variables.
 * See README.md for the full list of required env vars.
 */
@ConfigurationProperties(prefix = "thingspace")
public class ThingSpaceProperties {

    /** OAuth client id/secret for the thingspace_oauth scheme. */
    private String oauthClientId;
    private String oauthClientSecret;

    /** Custom header credential for VZ-M2M-Token. */
    private String vzM2mToken;

    /** Your ThingSpace account name, e.g. "0251687987-0001". */
    private String accountName;

    /**
     * Open request IDs to poll for status — these are the UUIDs returned when you
     * originally submitted an order/activation request. AccountRequestsController's
     * only endpoint is getCurrentAsynchronousRequestStatus(accountName, requestId),
     * so this list is what drives the polling loop (not device IDs).
     */
    private List<String> requestIds;

    /** Which environment to hit: PRODUCTION or MOCK_SERVER_FOR_LIMITED_AVAILABILITY_SEE_QUICK_START. */
    private String environment = "PRODUCTION";

    /** Optional overrides for the API base URLs. */
    private String baseUrl;
    private String credentialBaseUrl;

    /** Poll interval in milliseconds. */
    private long pollIntervalMs = 300_000; // 5 minutes

    // --- getters/setters ---

    public String getOauthClientId() { return oauthClientId; }
    public void setOauthClientId(String oauthClientId) { this.oauthClientId = oauthClientId; }

    public String getOauthClientSecret() { return oauthClientSecret; }
    public void setOauthClientSecret(String oauthClientSecret) { this.oauthClientSecret = oauthClientSecret; }

    public String getVzM2mToken() { return vzM2mToken; }
    public void setVzM2mToken(String vzM2mToken) { this.vzM2mToken = vzM2mToken; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public List<String> getRequestIds() { return requestIds; }
    public void setRequestIds(List<String> requestIds) { this.requestIds = requestIds; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getCredentialBaseUrl() { return credentialBaseUrl; }
    public void setCredentialBaseUrl(String credentialBaseUrl) { this.credentialBaseUrl = credentialBaseUrl; }

    public long getPollIntervalMs() { return pollIntervalMs; }
    public void setPollIntervalMs(long pollIntervalMs) { this.pollIntervalMs = pollIntervalMs; }
}
