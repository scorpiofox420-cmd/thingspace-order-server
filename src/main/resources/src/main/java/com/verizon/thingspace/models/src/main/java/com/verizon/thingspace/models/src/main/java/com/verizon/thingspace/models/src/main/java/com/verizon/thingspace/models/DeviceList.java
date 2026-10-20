package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceList {
    private final List<DeviceId> deviceIds;

    private DeviceList(List<DeviceId> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public List<DeviceId> getDeviceIds() { return deviceIds; }

    public static class Builder {
        private final List<DeviceId> deviceIds;

        public Builder(List<DeviceId> deviceIds) {
            this.deviceIds = deviceIds;
        }

        public DeviceList build() {
            return new DeviceList(deviceIds);
        }
    }
}
