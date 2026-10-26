package com.verizon.thingspace.controllers;

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

    @PostMapping("/requests/status")
    public ResponseEntity<Object> uploadDeviceIdentifier(@RequestBody Object body) throws Exception {
        String url = client.baseUrl() + "/m2m/v1/devices/requests/status";
        VerizonClient.HttpResult result = client.request("POST", url, body);
        result.requireOk();
        Object parsed = client.mapper().treeToValue(result.json(), Object.class);
        return ResponseEntity.status(result.statusCode()).body(parsed);
    }

    @PostMapping("/actions/activate")
    public ResponseEntity<Object> activateDevices(@RequestBody Object body) throws Exception {
        String url = client.baseUrl() + "/m2m/v1/devices/actions/activate";
        VerizonClient.HttpResult result = client.request("POST", url, body);
        result.requireOk();
        Object parsed = client.mapper().treeToValue(result.json(), Object.class);
        return ResponseEntity.status(result.statusCode()).body(parsed);
    }
}
