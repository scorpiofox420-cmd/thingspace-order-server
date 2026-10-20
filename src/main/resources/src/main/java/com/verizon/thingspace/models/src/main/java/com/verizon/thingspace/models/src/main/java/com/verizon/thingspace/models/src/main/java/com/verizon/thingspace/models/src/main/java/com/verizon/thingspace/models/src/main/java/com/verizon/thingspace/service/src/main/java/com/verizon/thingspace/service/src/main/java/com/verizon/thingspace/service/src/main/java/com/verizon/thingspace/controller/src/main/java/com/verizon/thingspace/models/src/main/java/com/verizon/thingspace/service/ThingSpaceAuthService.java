package com.verizon.thingspace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashMap;
import java.util.Map;

@Service
public class ThingSpaceAuthService {
    private final WebClient webClient = WebClient.create("https://thingspace.verizon.com/api/m2m/v1");

    public String authenticate(String username, String password) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);

            Map response = webClient.post()
                .uri("/session/login")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (response != null && response.containsKey("sessionToken")) {
                return (String) response.get("sessionToken");
            }
            throw new RuntimeException("Session token field missing from ThingSpace response.");
        } catch (Exception e) {
            throw new RuntimeException("ThingSpace Authentication failed: " + e.getMessage());
        }
    }
}
