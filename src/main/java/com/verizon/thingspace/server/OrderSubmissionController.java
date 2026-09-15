package com.verizon.thingspace.server;

import com.verizon.m5gedge.VerizonClient;
import com.verizon.m5gedge.http.response.ApiResponse;
import com.verizon.m5gedge.models.CheckOrderStatusRequest;
import com.verizon.m5gedge.models.DeviceId;
import com.verizon.m5gedge.models.DeviceList;
import com.verizon.m5gedge.models.DeviceManagementResult;
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
 * Confirmed against real SDK source (io.sdks:verizon-apis-sdk:1.7.0):
 *   - DeviceManagementController.uploadDeviceIdentifier(CheckOrderStatusRequest body)
 *     -> POST /m2m/v1/devices/requests/status
 *     -> ApiResponse<DeviceManagementResult> where DeviceManagementResult has a single
 *        field: requestId (String) — a unique string that associates the request with
 *        results sent via a callback service.
 *   - Auth required: thingspace_oauth + VZ-M2M-Token (no session token needed)
 *
 * Per Verizon's docs, calling this does NOT return the order status in the response —
 * it triggers an async callback to whatever URL you've registered (see CallbackController).
 * The response to this call just confirms the request was accepted and returns a requestId
 * so you can track the callback.
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
        DeviceList deviceList = new DeviceList.Builder().deviceIds(List.of(deviceId)).build();

        CheckOrderStatusRequest body = new CheckOrderStatusRequest.Builder(
                req.accountName(),
                List.of(deviceList)
        ).orderRequestId(req.orderRequestId()).build();

        log.info("Submitting CheckOrderStatus: accountName={}, orderRequestId={}, deviceId={}",
                req.accountName(), req.orderRequestId(), req.deviceId());

        ApiResponse<DeviceManagementResult> response =
                verizonClient.getDeviceManagementController().uploadDeviceIdentifier(body);

        DeviceManagementResult result = response.getResult();

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
