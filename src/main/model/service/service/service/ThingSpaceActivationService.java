package com.example.thingspace.service;

import com.example.thingspace.model.DeviceActivationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ThingSpaceActivationService {

    private final WebClient webClient = WebClient.create("https://thingspace.verizon.com/api/m2m/v1");
    private final ThingSpaceAuthService authService;

    public ThingSpaceActivationService(ThingSpaceAuthService authService) {
        this.authService = authService;
    }

    public String submitActivation(String username, String password, DeviceActivationRequest payload) {
        String m2mToken = authService.authenticate(username, password);

        return webClient.post()
            .uri("/devices/actions/activate")
            .header("Content-Type", "application/json")
            .header("VZ-M2M-Token", m2mToken)
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
