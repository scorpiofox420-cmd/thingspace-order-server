package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivateDevicesRequest {

    private List<DeviceActivation> devices;
    private String servicePlan;
    private String mdnZipCode;
    private String accountName;

    public ActivateDevicesRequest() {}

    public ActivateDevicesRequest(List<DeviceActivation> devices, String servicePlan, String mdnZipCode) {
        this.devices = devices;
        this.servicePlan = servicePlan;
        this.mdnZipCode = mdnZipCode;
    }

    @JsonProperty("devices")
    public List<DeviceActivation> getDevices() { return devices; }
    public void setDevices(List<DeviceActivation> devices) { this.devices = devices; }

    @JsonProperty("servicePlan")
    public String getServicePlan() { return servicePlan; }
    public void setServicePlan(String servicePlan) { this.servicePlan = servicePlan; }

    @JsonProperty("mdnZipCode")
    public String getMdnZipCode() { return mdnZipCode; }
    public void setMdnZipCode(String mdnZipCode) { this.mdnZipCode = mdnZipCode; }

    @JsonProperty("accountName")
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
}
