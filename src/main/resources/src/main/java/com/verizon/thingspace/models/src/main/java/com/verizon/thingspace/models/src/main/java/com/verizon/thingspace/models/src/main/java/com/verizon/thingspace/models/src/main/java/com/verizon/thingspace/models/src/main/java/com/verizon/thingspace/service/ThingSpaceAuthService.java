package com.verizon.thingspace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class ThingSpaceAuthService {
    private final WebClient webClient = WebClient.create("https://thingspace.verizon.com/api/m2m/v1");

    public String authenticate(String username, String password) {
        try {
            Map response = webClient.post()
                .uri("/sessions")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            if (response != null && response.containsKey("sessionToken")) {
                return (String) response.get("sessionToken");
            }
            return "";
        } catch (Exception e) {
            throw new RuntimeException("ThingSpace Authentication failed: " + e.getMessage());
        }
    }
}
