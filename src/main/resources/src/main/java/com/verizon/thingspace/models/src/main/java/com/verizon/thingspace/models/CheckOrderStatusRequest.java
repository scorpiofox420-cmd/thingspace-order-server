package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckOrderStatusRequest {

    private final String accountName;
    private final String orderRequestId;
    private final List<DeviceList> devices;

    private CheckOrderStatusRequest(String accountName, String orderRequestId, List<DeviceList> devices) {
        this.accountName = accountName;
        this.orderRequestId = orderRequestId;
        this.devices = devices;
    }

    public String getAccountName() { return accountName; }
    public String getOrderRequestId() { return orderRequestId; }
    public List<DeviceList> getDevices() { return devices; }

    public static class Builder {
        private final String accountName;
        private final List<DeviceList> devices;
        private String orderRequestId;

        public Builder(String accountName, List<DeviceList> devices) {
            this.accountName = accountName;
            this.devices = devices;
        }

        public Builder orderRequestId(String orderRequestId) {
            this.orderRequestId = orderRequestId;
            return this;
        }

        public CheckOrderStatusRequest build() {
            return new CheckOrderStatusRequest(accountName, orderRequestId, devices);
        }
    }
}
