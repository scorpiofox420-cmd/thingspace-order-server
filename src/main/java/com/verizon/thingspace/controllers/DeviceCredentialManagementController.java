package com.verizon.thingspace.controllers;

import com.verizon.thingspace.VerizonClient;
import com.verizon.thingspace.http.response.ApiResponse;
import com.verizon.thingspace.models.CredentialsRequest;
import com.verizon.thingspace.models.DropResponse;
import com.verizon.thingspace.models.GenerateResponse;
import com.verizon.thingspace.models.RetrieveResponse;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Maps to the Device Credential Management API (HYPER_PRECISE_CREDENTIALS server).
 *
 *   retrieveCredentials / generateCredentials / resetCredentials / dropCredentials
 *     -> POST {credentialBase}/{action}
 *
 * The published verizon-apis-sdk artifact does not ship this controller, so it is
 * implemented directly here against the documented request/response models. The
 * exact REST sub-paths are configurable via THINGSPACE_CREDENTIAL_BASE (defaults
 * to https://thingspace.verizon.com/api/hyper-precise/v1) with the standard
 * /credentials/{retrieve|generate|reset|drop} suffixes.
 */
public class DeviceCredentialManagementController {

    private final VerizonClient client;

    public DeviceCredentialManagementController(VerizonClient client) {
        this.client = client;
    }

    public ApiResponse<RetrieveResponse> retrieveCredentials(CredentialsRequest body) throws Exception {
        JsonNode node = call("/credentials/retrieve", body);
        return new ApiResponse<>(new RetrieveResponse(node), 200);
    }

    public ApiResponse<GenerateResponse> generateCredentials(CredentialsRequest body) throws Exception {
        JsonNode node = call("/credentials/generate", body);
        return new ApiResponse<>(new GenerateResponse(node), 200);
    }

    public ApiResponse<GenerateResponse> resetCredentials(CredentialsRequest body) throws Exception {
        JsonNode node = call("/credentials/reset", body);
        return new ApiResponse<>(new GenerateResponse(node), 200);
    }

    public ApiResponse<DropResponse> dropCredentials(CredentialsRequest body) throws Exception {
        JsonNode node = call("/credentials/drop", body);
        return new ApiResponse<>(new DropResponse(node), 200);
    }

    private JsonNode call(String path, CredentialsRequest body) {
        if (body == null) {
            throw new IllegalArgumentException("body is required");
        }
        String url = client.credentialBaseUrl() + path;
        VerizonClient.HttpResult result = client.request("POST", url, body);
        result.requireOk();
        return result.json();
    }
}