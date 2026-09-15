package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * POST body for the Device Credential Management endpoints
 * (retrieve / generate / reset / drop).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CredentialsRequest {

    private final String accountName;
    private final String servicePlan;
    private final List<DeviceCredentialRequestItem> devices;

    private CredentialsRequest(String accountName, String servicePlan, List<DeviceCredentialRequestItem> devices) {
        this.accountName = accountName;
        this.servicePlan = servicePlan;
        this.devices = devices;
    }

    public String getAccountName() { return accountName; }
    public String getServicePlan() { return servicePlan; }
    public List<DeviceCredentialRequestItem> getDevices() { return devices; }

    public static class Builder {
        private final String accountName;
        private final String servicePlan;
        private final List<DeviceCredentialRequestItem> devices;

        public Builder(String accountName, String servicePlan, List<DeviceCredentialRequestItem> devices) {
            this.accountName = accountName;
            this.servicePlan = servicePlan;
            this.devices = devices;
        }

        public CredentialsRequest build() {
            return new CredentialsRequest(accountName, servicePlan, devices);
        }
    }
}