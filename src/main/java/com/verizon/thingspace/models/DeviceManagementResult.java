package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Result of submitting a Check Order Status request — a single requestId field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceManagementResult {

    private String requestId;

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    @Override
    public String toString() {
        return "DeviceManagementResult{requestId='" + requestId + "'}";
    }
}