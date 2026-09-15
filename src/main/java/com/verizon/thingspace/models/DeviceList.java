package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * A list of device identifiers, used inside CheckOrderStatusRequest.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceList {

    private final List<DeviceId> deviceIds;

    private DeviceList(List<DeviceId> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public List<DeviceId> getDeviceIds() { return deviceIds; }

    public static class Builder {
        private List<DeviceId> deviceIds;

        public Builder() {}

        public Builder(List<DeviceId> deviceIds) {
            this.deviceIds = deviceIds;
        }

        public Builder deviceIds(List<DeviceId> deviceIds) {
            this.deviceIds = deviceIds;
            return this;
        }

        public DeviceList build() {
            return new DeviceList(deviceIds);
        }
    }
}