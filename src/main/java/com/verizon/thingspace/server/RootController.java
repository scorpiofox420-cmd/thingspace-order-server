package com.verizon.thingspace.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("service", "ThingSpace Order Server");
        m.put("status", "running");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("GET  /api/orders", "All collected request-status records");
        endpoints.put("GET  /api/orders/{requestId}", "Record for one request ID");
        endpoints.put("POST /api/orders/check-status", "Submit a Check Order Status request");
        endpoints.put("POST /api/orders/poll-now", "Trigger an immediate poll cycle");
        endpoints.put("GET  /api/device-orders", "Device-order manifest (line items)");
        endpoints.put("GET  /api/device-orders/summary", "Totals, per-model, delivery, add-on counts");
        endpoints.put("GET  /api/device-orders/customer", "Customer info");
        endpoints.put("GET  /api/device-details", "Per-device tracking (IMEI, phone, transfers)");
        endpoints.put("GET  /api/device-details/summary", "Transfer status summary");
        endpoints.put("POST /api/device-details", "Add a device detail");
        endpoints.put("PUT  /api/device-details/{id}", "Update a device detail");
        endpoints.put("POST /api/credentials/retrieve", "Retrieve device credentials");
        endpoints.put("POST /api/credentials/generate", "Generate device credentials");
        endpoints.put("POST /api/credentials/reset", "Reset device credentials");
        endpoints.put("POST /api/credentials/drop", "Drop device credentials");
        endpoints.put("POST /api/callbacks/device-service", "Webhook receiver for ThingSpace callbacks");
        endpoints.put("GET  /api/callbacks", "View raw callback payloads received");
        m.put("endpoints", endpoints);

        return m;
    }
}