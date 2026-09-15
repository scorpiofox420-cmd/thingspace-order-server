package com.verizon.thingspace.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device-details")
public class DeviceDetailController {

    private static final Logger log = LoggerFactory.getLogger(DeviceDetailController.class);

    private final DeviceDetailRepository deviceDetailRepository;

    public DeviceDetailController(DeviceDetailRepository deviceDetailRepository) {
        this.deviceDetailRepository = deviceDetailRepository;
    }

    @GetMapping
    public List<DeviceDetail> allDetails() {
        return deviceDetailRepository.findAll();
    }

    @PostMapping
    public DeviceDetail addDetail(@RequestBody DeviceDetail detail) {
        return deviceDetailRepository.save(detail);
    }

    @PutMapping("/{id}")
    public DeviceDetail updateDetail(@RequestBody DeviceDetail updated) {
        return deviceDetailRepository.save(updated);
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        List<DeviceDetail> details = deviceDetailRepository.findAll();

        long totalTransfers = details.stream()
                .filter(d -> d.getTransferStatus() != null && !d.getTransferStatus().isEmpty())
                .count();
        long successfulTransfers = details.stream()
                .filter(d -> "SUCCESSFUL".equals(d.getTransferStatus()))
                .count();
        long pendingTransfers = details.stream()
                .filter(d -> "IN_PROGRESS".equals(d.getTransferStatus()))
                .count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalDevicesTracked", details.size());
        result.put("totalTransfers", totalTransfers);
        result.put("successfulTransfers", successfulTransfers);
        result.put("pendingTransfers", pendingTransfers);
        result.put("devices", details);
        return result;
    }
}
