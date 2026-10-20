package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;

public class CredentialsRequest {
    private String eCPD;
    private String accountNumber;
    private List<DeviceCredentialRequestItem> items;

    public CredentialsRequest() {}

    public CredentialsRequest(String eCPD, String accountNumber, List<DeviceCredentialRequestItem> items) {
        this.eCPD = eCPD;
        this.accountNumber = accountNumber;
        this.items = items;
    }

    @JsonGetter("ECPD")
    public String getECPD() { return eCPD; }
    @JsonSetter("ECPD")
    public void setECPD(String eCPD) { this.eCPD = eCPD; }

    @JsonGetter("accountNumber")
    public String getAccountNumber() { return accountNumber; }
    @JsonSetter("accountNumber")
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    @JsonGetter("items")
    public List<DeviceCredentialRequestItem> getItems() { return items; }
    @JsonSetter("items")
    public void setItems(List<DeviceCredentialRequestItem> items) { this.items = items; }
}
