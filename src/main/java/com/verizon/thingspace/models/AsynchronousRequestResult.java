package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response from AccountRequestsController.getCurrentAsynchronousRequestStatus
 * — GET /m2m/v1/accounts/{aname}/requests/{requestId}/status.
 * Has exactly two fields: requestId and status.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsynchronousRequestResult {

    private String requestId;
    private String status;

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "AsynchronousRequestResult{requestId='" + requestId + "', status='" + status + "'}";
    }
}