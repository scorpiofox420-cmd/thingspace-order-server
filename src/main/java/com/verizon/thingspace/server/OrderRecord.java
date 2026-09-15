package com.verizon.thingspace.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A collected ThingSpace request/order status record.
 *
 * Created when a CheckOrderStatus request is submitted (status PENDING) and
 * updated by OrderPollingService / CallbackController as async results arrive.
 */
@Entity
@Table(name = "order_records")
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String status;

    @Column(name = "raw_response", columnDefinition = "text")
    private String rawResponse;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    protected OrderRecord() {}

    public OrderRecord(String requestId, String accountName, String status, String rawResponse) {
        this.requestId = requestId;
        this.accountName = accountName;
        this.status = status;
        this.rawResponse = rawResponse;
        this.collectedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getRequestId() { return requestId; }
    public String getAccountName() { return accountName; }
    public String getStatus() { return status; }
    public String getRawResponse() { return rawResponse; }
    public Instant getCollectedAt() { return collectedAt; }

    public void setStatus(String status) { this.status = status; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }
    public void setCollectedAt(Instant collectedAt) { this.collectedAt = collectedAt; }
}