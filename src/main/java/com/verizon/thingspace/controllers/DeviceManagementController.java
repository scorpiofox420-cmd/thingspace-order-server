package com.verizon.thingspace.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.verizon.thingspace.VerizonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceManagementController {

    private final VerizonClient client;

    public DeviceManagementController(VerizonClient client) {
        this.client = client;
    }

    /**
     * Health/Status check or proxy endpoint for device operations.
     */
    @GetMapping("/status")
    public ResponseEntity<JsonNode> checkStatus() {
        JsonNode responseNode = client.mapper().createObjectNode()
                .put("status", "ONLINE")
                .put("service", "ThingSpace Device Management Controller");
        return ResponseEntity.ok(responseNode);
    }

    /**
     * Example endpoint to proxy device actions or queries via VerizonClient.
     */
    @GetMapping("/info")
    public ResponseEntity<JsonNode> getDeviceInfo(
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String imei) {
        
        // If needed, you can implement direct Verizon API calls here using client.request(...)
        JsonNode infoNode = client.mapper().createObjectNode()
                .put("accountName", accountName != null ? accountName : "default-account")
                .put("imei", imei != null ? imei : "unknown")
                .put("message", "Device controller is active and ready.");
                
        return ResponseEntity.ok(infoNode);
    }
}
