package com.verizon.thingspace.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Raw storage for whatever ThingSpace posts to our callback URL after a
 * Check Order Status request. Kept as raw JSON (rather than a parsed model)
 * since the exact callback payload shape wasn't in any source confirmed so far —
 * see CallbackController for details on where to tighten this up once you've
 * seen a real payload land.
 */
@Entity
@Table(name = "callback_records")
public class CallbackRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_payload", columnDefinition = "text", nullable = false)
    private String rawPayload;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    protected CallbackRecord() {
        // required by JPA
    }

    public CallbackRecord(String rawPayload) {
        this.rawPayload = rawPayload;
        this.receivedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getRawPayload() { return rawPayload; }
    public Instant getReceivedAt() { return receivedAt; }
}
