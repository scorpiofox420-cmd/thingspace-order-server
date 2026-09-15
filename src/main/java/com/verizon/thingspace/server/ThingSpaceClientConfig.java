package com.verizon.thingspace.server;

import com.verizon.m5gedge.Environment;
import com.verizon.m5gedge.VerizonClient;
import com.verizon.m5gedge.authentication.ThingspaceOauthModel;
import com.verizon.m5gedge.authentication.VZM2mTokenModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the VerizonClient bean once at startup from ThingSpaceProperties.
 *
 * Confirmed against real SDK source (io.sdks:verizon-apis-sdk:1.7.0):
 *   - VerizonClient.Builder takes Model objects (ThingspaceOauthModel, VZM2mTokenModel),
 *     each built via a constructor-based Builder.
 *   - Environment enum only has PRODUCTION and
 *     MOCK_SERVER_FOR_LIMITED_AVAILABILITY_SEE_QUICK_START — no STAGING/DEV/QA.
 */
@Configuration
@EnableConfigurationProperties(ThingSpaceProperties.class)
public class ThingSpaceClientConfig {

    @Bean
    public VerizonClient verizonClient(ThingSpaceProperties properties) {
        ThingspaceOauthModel oauthModel = new ThingspaceOauthModel.Builder(
                properties.getOauthClientId(),
                properties.getOauthClientSecret()
        ).build();

        VZM2mTokenModel vzModel = new VZM2mTokenModel.Builder(
                properties.getVzM2mToken()
        ).build();

        return new VerizonClient.Builder()
                .thingspaceOauthCredentials(oauthModel)
                .vZM2mTokenCredentials(vzModel)
                .environment(Environment.valueOf(properties.getEnvironment()))
                .build();
    }
}
