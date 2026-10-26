package com.verizon.thingspace.controllers;

import com.verizon.thingspace.VerizonClient;
import com.verizon.thingspace.http.response.ApiResponse;
import com.verizon.thingspace.models.CheckOrderStatusRequest;
import com.verizon.thingspace.models.ActivateDevicesRequest;
import com.verizon.thingspace.models.DeviceManagementResult;
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
    public ResponseEntity<DeviceManagementResult> uploadDeviceIdentifier(
            @RequestBody CheckOrderStatusRequest body) throws Exception {
        
        String url = client.baseUrl() + "/m2m/v1/devices/requests/status";
        VerizonClient.HttpResult result = client.request("POST", url, body);
        result.requireOk();
        
        DeviceManagementResult parsed = client.mapper().treeToValue(
            result.json(), DeviceManagementResult.class);
            
        return ResponseEntity.status(result.statusCode()).body(parsed);
    }

    @PostMapping("/actions/activate")
    public ResponseEntity<DeviceManagementResult> activateDevices(
            @RequestBody ActivateDevicesRequest body) throws Exception {
        
        String url = client.baseUrl() + "/m2m/v1/devices/actions/activate";
        VerizonClient.HttpResult result = client.request("POST", url, body);
        result.requireOk();
        
        DeviceManagementResult parsed = client.mapper().treeToValue(
            result.json(), DeviceManagementResult.class);
            
        return ResponseEntity.status(result.statusCode()).body(parsed);
    }
}
