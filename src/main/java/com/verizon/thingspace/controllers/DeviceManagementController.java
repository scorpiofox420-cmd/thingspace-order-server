package com.verizon.thingspace.controllers;

import com.verizon.thingspace.VerizonClient;
import com.verizon.thingspace.http.response.ApiResponse;
import com.verizon.thingspace.models.CheckOrderStatusRequest;
import com.verizon.thingspace.models.DeviceManagementResult;

import java.io.IOException;

/**
 * Maps to the Device Management API.
 *
 *   uploadDeviceIdentifier(CheckOrderStatusRequest body)
 *     -> POST /m2m/v1/devices/requests/status
 *     -> ApiResponse<DeviceManagementResult> (field: requestId)
 */
public class DeviceManagementController {

    private final VerizonClient client;

    public DeviceManagementController(VerizonClient client) {
        this.client = client;
    }

    public ApiResponse<DeviceManagementResult> uploadDeviceIdentifier(CheckOrderStatusRequest body)
            throws Exception {
        if (body == null) {
            throw new IllegalArgumentException("body is required");
        }
        String url = client.baseUrl() + "/m2m/v1/devices/requests/status";

        VerizonClient.HttpResult result = client.request("POST", url, body);
        result.requireOk();

        DeviceManagementResult parsed = client.mapper().treeToValue(
                result.json(), DeviceManagementResult.class);
        return new ApiResponse<>(parsed, result.statusCode());
    }
}