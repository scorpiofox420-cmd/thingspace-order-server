package com.verizon.thingspace.server;

import com.verizon.thingspace.VerizonClient;
import com.verizon.thingspace.models.CheckOrderStatusRequest;
import com.verizon.thingspace.models.DeviceList;
import com.verizon.thingspace.models.DeviceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Submits a Check Order Status request to ThingSpace.
 *
 * Confirmed against real SDK source:
 *   - DeviceManagementController.uploadDeviceIdentifier(CheckOrderStatusRequest body)
 *     -> POST /m2m/v1/devices/requests/status
 *     -> ApiResponse<DeviceManagementResult> (field: requestId)
 *   - Auth: thingspace_oauth + VZ-M2M-Token
 *
 * Response just confirms the request was accepted; actual status arrives
 * via async callback (see CallbackController).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderSubmissionController {

    private static final Logger log = LoggerFactory.getLogger(OrderSubmissionController.class);

    private final VerizonClient verizonClient;
    private final OrderRepository orderRepository;

    public OrderSubmissionController(VerizonClient verizonClient, OrderRepository orderRepository) {
        this.verizonClient = verizonClient;
        this.orderRepository = orderRepository;
    }

    public record CheckStatusApiRequest(
            String accountName,
            String orderRequestId,
            String deviceId,
            String deviceIdKind
    ) {}

    @PostMapping("/check-status")
    public Map<String, String> checkOrderStatus(@RequestBody CheckStatusApiRequest req) throws Exception {

        DeviceId deviceId = new DeviceId.Builder(req.deviceId(), req.deviceIdKind()).build();
        DeviceList deviceList = new DeviceList.Builder(List.of(deviceId)).build();

        CheckOrderStatusRequest body = new CheckOrderStatusRequest.Builder(
                req.accountName(),
                List.of(deviceList)
        ).orderRequestId(req.orderRequestId()).build();

        log.info("Submitting CheckOrderStatus: accountName={}, orderRequestId={}, deviceId={}",
                req.accountName(), req.orderRequestId(), req.deviceId());

        var response = verizonClient.getDeviceManagementController()
                .uploadDeviceIdentifier(body);

        var result = response.getResult();

        if (result != null && result.getRequestId() != null) {
            String requestId = result.getRequestId();
            log.info("CheckOrderStatus accepted — requestId={}", requestId);

            orderRepository.save(new OrderRecord(
                    requestId, req.accountName(), "PENDING", result.toString()));

            return Map.of(
                    "status", "accepted",
                    "requestId", requestId,
                    "accountName", req.accountName()
            );
        }

        log.warn("CheckOrderStatus returned empty result body");
        return Map.of(
                "status", "accepted",
                "accountName", req.accountName()
        );
    }
}
