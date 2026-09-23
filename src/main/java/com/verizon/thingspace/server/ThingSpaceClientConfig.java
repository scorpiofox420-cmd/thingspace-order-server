package com.verizon.thingspace.server;

import com.verizon.thingspace.VerizonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the internal VerizonClient bean once at startup from ThingSpaceProperties.
 *
 * The client is self-contained (no external SDK jar): it uses the base URLs below,
 * which are the ones verified from the official SDK sources, overridable via
 * THINGSPACE_BASE_URL / THINGSPACE_CREDENTIAL_BASE_URL.
 */
@Configuration
@EnableConfigurationProperties(ThingSpaceProperties.class)
public class ThingSpaceClientConfig {

    private static final String PROD_BASE = "https://thingspace.verizon.com/api";
    private static final String PROD_CREDENTIALS = "https://thingspace.verizon.com/api/hyper-precise/v1";
    private static final String MOCK_BASE = "https://mock.thingspace.verizon.com/api";
    private static final String MOCK_CREDENTIALS = "https://mock.thingspace.verizon.com/api/hyper-precise/v1";

    @Bean
    public VerizonClient verizonClient(ThingSpaceProperties properties) {
        boolean mock = properties.getEnvironment() != null
                && properties.getEnvironment().toUpperCase().startsWith("MOCK");
        String baseUrl = firstNonNull(properties.getBaseUrl(), mock ? MOCK_BASE : PROD_BASE);
        String credentialBaseUrl = firstNonNull(properties.getCredentialBaseUrl(),
                mock ? MOCK_CREDENTIALS : PROD_CREDENTIALS);

        return new VerizonClient(
                baseUrl,
                credentialBaseUrl,
                properties.getOauthClientId(),
                properties.getOauthClientSecret(),
                properties.getVzM2mToken(),
                properties.getUwsUsername(),
                properties.getUwsPassword(),
                properties.getOauthClockSkew());
    }

    private static String firstNonNull(String a, String b) {
        return a == null || a.isEmpty() ? b : a;
    }
}