package com.verizon.thingspace.server;

import com.verizon.thingspace.Environment;
import com.verizon.thingspace.VerizonClient;
import com.verizon.thingspace.authentication.ThingspaceOauthModel;
import com.verizon.thingspace.authentication.VZM2mTokenModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the VerizonClient bean once at startup from ThingSpaceProperties.
 *
 * Confirmed against real SDK source: the Builder takes Model objects
 * (ThingspaceOauthModel, VZM2mTokenModel), each built via a constructor.
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
