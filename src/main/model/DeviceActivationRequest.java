package com.example.thingspace.model;

import java.util.List;

public class DeviceActivationRequest {
    private String accountNumber;
    private String servicePlan;
    private List<DeviceInfo> devices;

    public DeviceActivationRequest() {}

    public DeviceActivationRequest(String accountNumber, String servicePlan, List<DeviceInfo> devices) {
        this.accountNumber = accountNumber;
        this.servicePlan = servicePlan;
        this.devices = devices;
    }

    public static class DeviceInfo {
        private String imei;
        private String iccid;

        public DeviceInfo() {}

        public DeviceInfo(String imei, String iccid) {
            this.imei = imei;
            this.iccid = iccid;
        }

        public String getImei() { return imei; }
        public void setImei(String imei) { this.imei = imei; }
        public String getIccid() { return iccid; }
        public void setIccid(String iccid) { this.iccid = iccid; }
    }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getServicePlan() { return servicePlan; }
    public void setServicePlan(String servicePlan) { this.servicePlan = servicePlan; }
    public List<DeviceInfo> getDevices() { return devices; }
    public void setDevices(List<DeviceInfo> devices) { this.devices = devices; }
}
