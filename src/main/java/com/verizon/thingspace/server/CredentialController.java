package com.verizon.thingspace.server;

import com.verizon.thingspace.VerizonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private static final Logger log = LoggerFactory.getLogger(CredentialController.class);

    private final VerizonClient verizonClient;

    public CredentialController(VerizonClient verizonClient) {
        this.verizonClient = verizonClient;
    }

    @PostMapping("/retrieve")
    public Map<String, Object> retrieveCredentials(@RequestBody Map<String, Object> body) {
        log.info("retrieveCredentials called: {}", body);
        try {
            var controller = verizonClient.getDeviceCredentialManagementController();
            log.info("DeviceCredentialManagementController obtained: {}", controller.getClass().getName());
            return Map.of("status", "retrieve — awaiting SDK method implementation",
                    "received", body);
        } catch (Exception e) {
            log.error("Failed to get DeviceCredentialManagementController: {}", e.getMessage());
            return Map.of("status", "error", "error", e.getMessage());
        }
    }

    @PostMapping("/generate")
    public Map<String, Object> generateCredentials(@RequestBody Map<String, Object> body) {
        log.info("generateCredentials called: {}", body);
        try {
            var controller = verizonClient.getDeviceCredentialManagementController();
            return Map.of("status", "generate — awaiting SDK method implementation",
                    "received", body);
        } catch (Exception e) {
            log.error("Failed to get DeviceCredentialManagementController: {}", e.getMessage());
            return Map.of("status", "error", "error", e.getMessage());
        }
    }

    @PostMapping("/reset")
    public Map<String, Object> resetCredentials(@RequestBody Map<String, Object> body) {
        log.info("resetCredentials called: {}", body);
        try {
            var controller = verizonClient.getDeviceCredentialManagementController();
            return Map.of("status", "reset — awaiting SDK method implementation",
                    "received", body);
        } catch (Exception e) {
            log.error("Failed to get DeviceCredentialManagementController: {}", e.getMessage());
            return Map.of("status", "error", "error", e.getMessage());
        }
    }

    @PostMapping("/drop")
    public Map<String, Object> dropCredentials(@RequestBody Map<String, Object> body) {
        log.info("dropCredentials called: {}", body);
        try {
            var controller = verizonClient.getDeviceCredentialManagementController();
            return Map.of("status", "drop — awaiting SDK method implementation",
                    "received", body);
        } catch (Exception e) {
            log.error("Failed to get DeviceCredentialManagementController: {}", e.getMessage());
            return Map.of("status", "error", "error", e.getMessage());
        }
    }
}
