package com.thingspace.order.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "verified_orders")
public class VerifiedOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;
    private String handshakeId;
    private String customerName;
    private String phoneNumber;
    private String zipCode;
    private String confirmationCode;
    private String storePickupLocation;
    private String totalPaid;
    
    @Column(length = 1000)
    private String verificationHash;
    
    private Instant handshakeTimestamp;
    private boolean pickupReady;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getHandshakeId() { return handshakeId; }
    public void setHandshakeId(String handshakeId) { this.handshakeId = handshakeId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }
    public String getStorePickupLocation() { return storePickupLocation; }
    public void setStorePickupLocation(String storePickupLocation) { this.storePickupLocation = storePickupLocation; }
    public String getTotalPaid() { return totalPaid; }
    public void setTotalPaid(String totalPaid) { this.totalPaid = totalPaid; }
    public String getVerificationHash() { return verificationHash; }
    public void setVerificationHash(String verificationHash) { this.verificationHash = verificationHash; }
    public Instant getHandshakeTimestamp() { return handshakeTimestamp; }
    public void setHandshakeTimestamp(Instant handshakeTimestamp) { this.handshakeTimestamp = handshakeTimestamp; }
    public boolean isPickupReady() { return pickupReady; }
    public void setPickupReady(boolean pickupReady) { this.pickupReady = pickupReady; }
}
