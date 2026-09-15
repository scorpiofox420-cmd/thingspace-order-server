package com.verizon.thingspace.server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderRecord, Long> {
    Optional<OrderRecord> findByRequestId(String requestId);
}
