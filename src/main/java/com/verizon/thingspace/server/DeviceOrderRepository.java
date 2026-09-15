package com.verizon.thingspace.server;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceOrderRepository extends JpaRepository<DeviceOrderItem, Long> {
}
