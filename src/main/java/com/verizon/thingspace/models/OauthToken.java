package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Object for storing information about the OAuth 2 access token.
 *
 * Mirrors the APIMATIC-generated VerizonLib model exactly (same wire field
 * names) so Jackson can round-trip the ThingSpace OAuth response verbatim:
 *   access_token, token_type, expires_in, scope, expiry, refresh_token
 *
 * <p>{@code expiry} is the unix timestamp (UTC) at which the token expires,
 * returned directly by the API. When present it takes precedence over
 * {@code expires_in}; otherwise the expiry is computed from {@code expires_in}.
 */
public class OauthToken {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String scope;
    private Long expiry;
    private String refreshToken;

    public OauthToken() {
    }

    /**
     * @param accessToken  Access token
     * @param tokenType    Type of access token
     * @param expiresIn    Time in seconds before the access token expires
     * @param scope        List of scopes granted (space-delimited strings)
     * @param expiry       Time of token expiry as unix timestamp (UTC)
     * @param refreshToken Refresh token used to get a new access token when it expires
     */
    public OauthToken(String accessToken, String tokenType,
                      Long expiresIn, String scope, Long expiry, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.expiry = expiry;
        this.refreshToken = refreshToken;
    }

    @JsonGetter("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonSetter("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonGetter("token_type")
    public String getTokenType() {
        return tokenType;
    }

    @JsonSetter("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @JsonGetter("expires_in")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long getExpiresIn() {
        return expiresIn;
    }

    @JsonSetter("expires_in")
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @JsonGetter("scope")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getScope() {
        return scope;
    }

    @JsonSetter("scope")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonGetter("expiry")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long getExpiry() {
        return expiry;
    }

    @JsonSetter("expiry")
    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    @JsonGetter("refresh_token")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getRefreshToken() {
        return refreshToken;
    }

    @JsonSetter("refresh_token")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Resolves the expiry unix timestamp (UTC): the API-returned {@code expiry}
     * if present, otherwise computed from {@code expires_in} (default 3600s).
     */
    public long resolveExpiryEpochSec() {
        if (expiry != null && expiry > 0) {
            return expiry;
        }
        long ttl = expiresIn != null && expiresIn > 0 ? expiresIn : 3600L;
        return Instant.now().getEpochSecond() + ttl;
    }

    public long getExpiryEpochSec() {
        return resolveExpiryEpochSec();
    }

    public LocalDateTime getExpiresAtUtc() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(resolveExpiryEpochSec()), ZoneOffset.UTC);
    }

    public boolean isExpired() {
        return isExpired(0L);
    }

    /** Clock-skew-tolerant expiry check, mirroring OAuthClockSkew handling. */
    public boolean isExpired(long clockSkewSec) {
        return Instant.now().getEpochSecond() + clockSkewSec >= resolveExpiryEpochSec();
    }
}