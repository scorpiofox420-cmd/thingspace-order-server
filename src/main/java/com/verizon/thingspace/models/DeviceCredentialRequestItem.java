package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One device entry inside CredentialsRequest.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceCredentialRequestItem {

    private final String deviceId;
    private final String kind;

    private DeviceCredentialRequestItem(String deviceId, String kind) {
        this.deviceId = deviceId;
        this.kind = kind;
    }

    public String getDeviceId() { return deviceId; }
    public String getKind() { return kind; }

    public static class Builder {
        private final String deviceId;
        private String kind;

        public Builder(String deviceId) {
            this.deviceId = deviceId;
        }

        public Builder kind(String kind) {
            this.kind = kind;
            return this;
        }

        public DeviceCredentialRequestItem build() {
            return new DeviceCredentialRequestItem(deviceId, kind);
        }
    }
}