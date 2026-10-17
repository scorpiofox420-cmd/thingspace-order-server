package com.example.thingspace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class ThingSpaceAuthService {

    private final WebClient webClient = WebClient.create("https://thingspace.verizon.com/api/m2m/v1");

    public String authenticate(String username, String password) {
        Map<String, String> loginPayload = Map.of(
            "username", username,
            "password", password
        );

        Map<?, ?> response = webClient.post()
            .uri("/session/login")
            .header("Content-Type", "application/json")
            .bodyValue(loginPayload)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        return (String) response.get("token");
    }
}
