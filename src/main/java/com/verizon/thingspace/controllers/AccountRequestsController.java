package com.verizon.thingspace.controllers;

import com.verizon.thingspace.VerizonClient;
import com.verizon.thingspace.http.response.ApiResponse;
import com.verizon.thingspace.models.AsynchronousRequestResult;

import java.io.IOException;

/**
 * Maps to the Account Requests API.
 *
 *   getCurrentAsynchronousRequestStatus(accountName, requestId)
 *     -> GET /m2m/v1/accounts/{aname}/requests/{requestId}/status
 *     -> ApiResponse<AsynchronousRequestResult> (fields: requestId, status)
 */
public class AccountRequestsController {

    private final VerizonClient client;

    public AccountRequestsController(VerizonClient client) {
        this.client = client;
    }

    public ApiResponse<AsynchronousRequestResult> getCurrentAsynchronousRequestStatus(
            String accountName, String requestId) throws Exception {
        if (accountName == null || requestId == null) {
            throw new IllegalArgumentException("accountName and requestId are required");
        }
        String url = client.baseUrl() + "/m2m/v1/accounts/" + accountName
                + "/requests/" + requestId + "/status";

        VerizonClient.HttpResult result = client.request("GET", url, null);
        result.requireOk();

        AsynchronousRequestResult parsed = client.mapper().treeToValue(
                result.json(), AsynchronousRequestResult.class);
        return new ApiResponse<>(parsed, result.statusCode());
    }

    public ApiResponse<AsynchronousRequestResult> getCurrentAsynchronousRequestStatusAsync(
            String accountName, String requestId) throws Exception {
        return getCurrentAsynchronousRequestStatus(accountName, requestId);
    }
}