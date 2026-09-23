package com.verizon.thingspace.server;

import com.verizon.thingspace.VerizonClient;
import com.verizon.thingspace.models.CredentialsRequest;
import com.verizon.thingspace.models.DeviceCredentialRequestItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public Map<String, Object> retrieveCredentials(@RequestBody CredentialApiRequest req) {
        log.info("retrieveCredentials: accountName={}, servicePlan={}, devices={}",
                req.accountName(), req.servicePlan(), req.deviceIds());

        CredentialsRequest body = buildRequest(req);

        try {
            var response = verizonClient.getDeviceCredentialManagementController()
                    .retrieveCredentials(body);
            return Map.of("status", "success", "result", response.getResult().toString());
        } catch (Exception e) {
            log.error("retrieveCredentials failed: {}", e.getMessage(), e);
            return Map.of("status", "error", "error", e.getMessage());
        }
    }

    @PostMapping("/generate")
    public Map<String, Object> generateCredentials(@RequestBody CredentialApiRequest req) {
        log.info("generateCredentials: accountName={}, servicePlan={}, devices={}",
                req.accountName(), req.servicePlan(), req.deviceIds());

        CredentialsRequest body = buildRequest(req);

        try {
            var response = verizonClient.getDeviceCredentialManagementController()
                    .generateCredentials(body);
            return Map.of("status", "success", "result", response.getResult().toString());
        } catch (Exception e) {
            log.error("generateCredentials failed: {}", e.getMessage(), e);
            return Map.of("status", "error", "error", e.getMessage());
        }
    }

    @PostMapping("/reset")
    public Map<String, Object> resetCredentials(@RequestBody CredentialApiRequest req) {
        log.info("resetCredentials: accountName={}, servicePlan={}, devices={}",
                req.accountName(), req.servicePlan(), req.deviceIds());

        CredentialsRequest body = buildRequest(req);

        try {
            var response = verizonClient.getDeviceCredentialManagementController()
                    .resetCredentials(body);
            return Map.of("status", "success", "result", response.getResult().toString());
        } catch (Exception e) {
            log.error("resetCredentials failed: {}", e.getMessage(), e);
            return Map.of("status", "error", "error", e.getMessage());
        }
    }

    @PostMapping("/drop")
    public Map<String, Object> dropCredentials(@RequestBody CredentialApiRequest req) {
        log.info("dropCredentials: accountName={}, servicePlan={}, devices={}",
                req.accountName(), req.servicePlan(), req.deviceIds());

        CredentialsRequest body = buildRequest(req);

        try {
            var response = verizonClient.getDeviceCredentialManagementController()
                    .dropCredentials(body);
            return Map.of("status", "success", "result", response.getResult().toString());
        } catch (Exception e) {
            log.error("dropCredentials failed: {}", e.getMessage(), e);
            return Map.of("status", "error", "error", e.getMessage());
        }
    }

    public record CredentialApiRequest(
            String accountName,
            String servicePlan,
            String eCPD,
            List<String> deviceIds
    ) {}

    private CredentialsRequest buildRequest(CredentialApiRequest req) {
        List<DeviceCredentialRequestItem> items = req.deviceIds().stream()
                .map(id -> new DeviceCredentialRequestItem.Builder(id).build())
                .toList();

        return new CredentialsRequest.Builder(
                req.eCPD(),
                req.accountName(),
                items
        ).build();
    }
}
