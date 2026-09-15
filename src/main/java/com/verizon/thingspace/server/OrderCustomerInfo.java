package com.verizon.thingspace.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "order_customer_info")
public class OrderCustomerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected OrderCustomerInfo() {}

    public OrderCustomerInfo(String fullName, String addressLine1, String addressLine2,
                             String city, String state, String zipCode,
                             String email, String phone, String pickupLocation) {
        this.fullName = fullName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.email = email;
        this.phone = phone;
        this.pickupLocation = pickupLocation;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPickupLocation() { return pickupLocation; }
    public Instant getCreatedAt() { return createdAt; }
}
