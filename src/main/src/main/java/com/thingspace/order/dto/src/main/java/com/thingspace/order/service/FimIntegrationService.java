package com.thingspace.order.service;

import com.thingspace.order.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class FimIntegrationService {

    @Value("${fim.api.app-id:backend!}")
    private String appId;

    @Value("${fim.api.username}")
    private String username;

    @Value("${fim.api.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TOKEN_URL = "https://fim.api.us.fleetmatics.com/token";
    private static final String WORK_ORDER_URL = "https://fim.api.us.fleetmatics.com:443/pas/v1/workorders";

    public String fetchFimToken() {
        String authStr = username + ":" + password;
        String base64Auth = Base64.getEncoder().encodeToString(authStr.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Auth);
        headers.set("Accept", "application/json");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.GET, request, String.class);

        return response.getBody().replace("\"", "").trim();
    }

    public String sendOrderToVerizon(OrderRequest orderRequest) {
        String token = fetchFimToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", "Atmosphere atmosphere_app_id=" + appId + ", Bearer " + token);

        HttpEntity<OrderRequest> entity = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(WORK_ORDER_URL, entity, String.class);

        return response.getBody();
    }
}
