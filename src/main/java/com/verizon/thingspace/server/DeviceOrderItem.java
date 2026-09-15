package com.verizon.thingspace.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A single device-order line item representing a group of identical devices
 * in the current Verizon order.
 */
@Entity
@Table(name = "device_orders")
public class DeviceOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String storage;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "add_ons", columnDefinition = "text")
    private String addOns;

    @Column(name = "full_coverage", nullable = false)
    private boolean fullCoverage;

    @Column(name = "paid_in_full", nullable = false)
    private boolean paidInFull;

    @Column(name = "auto_pay", nullable = false)
    private boolean autoPay;

    @Column(name = "credit_check_verified", nullable = false)
    private boolean creditCheckVerified;

    @Column(name = "delivery_method", nullable = false)
    private String deliveryMethod;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected DeviceOrderItem() {}

    public DeviceOrderItem(String model, String color, String storage, int quantity,
                           String addOns, boolean fullCoverage, boolean paidInFull,
                           boolean autoPay, boolean creditCheckVerified, String deliveryMethod) {
        this.model = model;
        this.color = color;
        this.storage = storage;
        this.quantity = quantity;
        this.addOns = addOns;
        this.fullCoverage = fullCoverage;
        this.paidInFull = paidInFull;
        this.autoPay = autoPay;
        this.creditCheckVerified = creditCheckVerified;
        this.deliveryMethod = deliveryMethod;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public String getStorage() { return storage; }
    public int getQuantity() { return quantity; }
    public String getAddOns() { return addOns; }
    public boolean isFullCoverage() { return fullCoverage; }
    public boolean isPaidInFull() { return paidInFull; }
    public boolean isAutoPay() { return autoPay; }
    public boolean isCreditCheckVerified() { return creditCheckVerified; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public Instant getCreatedAt() { return createdAt; }
}
