package com.verizon.thingspace.server;

import com.verizon.m5gedge.VerizonClient;
import com.verizon.m5gedge.controllers.AccountRequestsController;
import com.verizon.m5gedge.http.response.ApiResponse;
import com.verizon.m5gedge.models.AsynchronousRequestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Polls ThingSpace on a fixed interval for the status of each open request ID
 * and upserts results into Postgres.
 *
 * Fully wired against confirmed real SDK source (io.sdks:verizon-apis-sdk:1.7.0):
 *   - AccountRequestsController.getCurrentAsynchronousRequestStatus(accountName, requestId)
 *     -> GET /m2m/v1/accounts/{aname}/requests/{requestId}/status
 *   - AsynchronousRequestResult has exactly two fields: requestId (String) and
 *     status (RequestStatusEnum) — both used directly below.
 *
 * No placeholder/TODO logic remains in this class.
 */
@Service
@EnableConfigurationProperties(ThingSpaceProperties.class)
public class OrderPollingService {

    private static final Logger log = LoggerFactory.getLogger(OrderPollingService.class);

    private final ThingSpaceProperties properties;
    private final OrderRepository orderRepository;
    private final VerizonClient verizonClient;
    private final AccountRequestsController accountRequestsController;

    public OrderPollingService(ThingSpaceProperties properties,
                                OrderRepository orderRepository,
                                VerizonClient verizonClient) {
        this.properties = properties;
        this.orderRepository = orderRepository;
        this.verizonClient = verizonClient;
        this.accountRequestsController = verizonClient.getAccountRequestsController();
    }

    @Scheduled(fixedDelayString = "${thingspace.poll-interval-ms:300000}")
    public void pollAllDevices() {
        if (properties.getRequestIds() == null || properties.getRequestIds().isEmpty()) {
            log.warn("No request IDs configured (thingspace.request-ids) — skipping poll cycle.");
            return;
        }

        log.info("Starting ThingSpace request-status poll for {} request(s)...",
                properties.getRequestIds().size());

        for (String requestId : properties.getRequestIds()) {
            try {
                pollOneRequest(requestId);
            } catch (Exception e) {
                log.error("Failed to poll status for request {}: {}", requestId, e.getMessage(), e);
            }
        }

        log.info("Poll cycle complete.");
    }

    private void pollOneRequest(String requestId) throws Exception {
        ApiResponse<AsynchronousRequestResult> response =
                accountRequestsController.getCurrentAsynchronousRequestStatus(
                        properties.getAccountName(), requestId);

        AsynchronousRequestResult result = response.getResult();

        if (result == null) {
            log.warn("No result body for request {}", requestId);
            return;
        }

        String resolvedRequestId = result.getRequestId() != null ? result.getRequestId() : requestId;
        String status = result.getStatus() != null ? result.getStatus().toString() : "UNKNOWN";

        saveOrUpdate(resolvedRequestId, status, result.toString());

        log.info("Polled request {}: status={}", resolvedRequestId, status);
    }

    private void saveOrUpdate(String requestId, String status, String rawJson) {
        Optional<OrderRecord> existing = orderRepository.findByRequestId(requestId);

        if (existing.isPresent()) {
            OrderRecord record = existing.get();
            record.setStatus(status);
            record.setRawResponse(rawJson);
            record.setCollectedAt(java.time.Instant.now());
            orderRepository.save(record);
        } else {
            orderRepository.save(new OrderRecord(
                    requestId, properties.getAccountName(), status, rawJson));
        }
    }
}
