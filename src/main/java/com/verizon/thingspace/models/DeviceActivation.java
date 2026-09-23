package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One device inside an activation request.
 *
 * Wire shape (from the official Connectivity Management examples):
 *   { "idType": "ICCID", "idValue": "89014103211118510720", "imei": "353018090012345" }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceActivation {

    private String idType;
    private String idValue;
    private String imei;

    public DeviceActivation() {
    }

    public DeviceActivation(String idType, String idValue, String imei) {
        this.idType = idType;
        this.idValue = idValue;
        this.imei = imei;
    }

    @JsonProperty("idType")
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }

    @JsonProperty("idValue")
    public String getIdValue() { return idValue; }
    public void setIdValue(String idValue) { this.idValue = idValue; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }
}