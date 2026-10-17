package com.example.thingspace.controller;

import com.example.thingspace.model.DeviceActivationRequest;
import com.example.thingspace.service.ThingSpaceActivationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thingspace")
public class ThingSpaceController {

    private final ThingSpaceActivationService activationService;

    @Value("${TS_USERNAME:}")
    private String username;

    @Value("${TS_PASSWORD:}")
    private String password;

    public ThingSpaceController(ThingSpaceActivationService activationService) {
        this.activationService = activationService;
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateDevices(@RequestBody DeviceActivationRequest request) {
        try {
            String response = activationService.submitActivation(username, password, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Activation failed: " + e.getMessage());
        }
    }
}
