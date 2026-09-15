package com.verizon.thingspace.server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceDetailRepository extends JpaRepository<DeviceDetail, Long> {
    List<DeviceDetail> findByDeviceModelAndColor(String deviceModel, String color);
}
