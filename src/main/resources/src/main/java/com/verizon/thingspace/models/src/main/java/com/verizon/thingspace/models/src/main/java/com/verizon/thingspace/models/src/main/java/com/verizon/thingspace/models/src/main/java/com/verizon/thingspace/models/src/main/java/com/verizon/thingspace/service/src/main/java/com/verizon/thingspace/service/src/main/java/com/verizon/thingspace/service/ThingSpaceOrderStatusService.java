package com.verizon.thingspace.service;

import com.verizon.thingspace.models.CheckOrderStatusRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ThingSpaceOrderStatusService {

    private final WebClient webClient = WebClient.create("https://thingspace.verizon.com/api/m2m/v1");
    private final ThingSpaceAuthService authService;

    public ThingSpaceOrderStatusService(ThingSpaceAuthService authService) {
        this.authService = authService;
    }

    public String checkStatus(String username, String password, CheckOrderStatusRequest payload) {
        String m2mToken = authService.authenticate(username, password);

        return webClient.post()
            .uri("/devices/requests/status")
            .header("Content-Type", "application/json")
            .header("VZ-M2M-Token", m2mToken)
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
