package com.verizon.thingspace.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "device_details")
public class DeviceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_model", nullable = false)
    private String deviceModel;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "storage", nullable = false)
    private String storage;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "imei")
    private String imei;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "transfer_status")
    private String transferStatus;

    @Column(name = "transfer_from_carrier")
    private String transferFromCarrier;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected DeviceDetail() {}

    public DeviceDetail(String deviceModel, String color, String storage, int lineNumber,
                        String imei, String phoneNumber, String transferStatus,
                        String transferFromCarrier) {
        this.deviceModel = deviceModel;
        this.color = color;
        this.storage = storage;
        this.lineNumber = lineNumber;
        this.imei = imei;
        this.phoneNumber = phoneNumber;
        this.transferStatus = transferStatus;
        this.transferFromCarrier = transferFromCarrier;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getDeviceModel() { return deviceModel; }
    public String getColor() { return color; }
    public String getStorage() { return storage; }
    public int getLineNumber() { return lineNumber; }
    public String getImei() { return imei; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getTransferStatus() { return transferStatus; }
    public String getTransferFromCarrier() { return transferFromCarrier; }
    public Instant getCreatedAt() { return createdAt; }

    public void setImei(String imei) { this.imei = imei; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setTransferStatus(String transferStatus) { this.transferStatus = transferStatus; }
}
