package com.verizon.thingspace.controller;

import com.verizon.thingspace.models.ActivateDevicesRequest;
import com.verizon.thingspace.models.CheckOrderStatusRequest;
import com.verizon.thingspace.service.ThingSpaceActivationService;
import com.verizon.thingspace.service.ThingSpaceOrderStatusService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thingspace")
public class ThingSpaceController {

    private final ThingSpaceActivationService activationService;
    private final ThingSpaceOrderStatusService orderStatusService;

    @Value("${TS_USERNAME:}")
    private String username;

    @Value("${TS_PASSWORD:}")
    private String password;

    public ThingSpaceController(ThingSpaceActivationService activationService, 
                                ThingSpaceOrderStatusService orderStatusService) {
        this.activationService = activationService;
        this.orderStatusService = orderStatusService;
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateDevices(@RequestBody ActivateDevicesRequest request) {
        try {
            String response = activationService.submitActivation(username, password, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Activation failed: " + e.getMessage());
        }
    }

    @PostMapping("/status")
    public ResponseEntity<String> checkOrderStatus(@RequestBody CheckOrderStatusRequest request) {
        try {
            String response = orderStatusService.checkStatus(username, password, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Status check failed: " + e.getMessage());
        }
    }
}
