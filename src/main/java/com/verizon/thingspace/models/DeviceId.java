package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A device identifier pair (e.g. IMEI) used inside CheckOrderStatusRequest.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceId {

    private final String deviceId;
    private final String kind;

    private DeviceId(String deviceId, String kind) {
        this.deviceId = deviceId;
        this.kind = kind;
    }

    public String getDeviceId() { return deviceId; }
    public String getKind() { return kind; }

    public static class Builder {
        private final String deviceId;
        private final String kind;

        public Builder(String deviceId, String kind) {
            this.deviceId = deviceId;
            this.kind = kind;
        }

        public DeviceId build() {
            return new DeviceId(deviceId, kind);
        }
    }
}