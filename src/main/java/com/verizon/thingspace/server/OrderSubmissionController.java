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
 * Confirmed request shape (from Verizon's docs):
 *   accountName (required), orderRequestId (optional), devices (required,
 *   List<DeviceList>, each containing deviceIds: List<{id, kind}>)
 *
 * Auth: thingspace_oauth + VZ-M2M-Token (no session token needed).
 * Response returns a requestId; actual status arrives via async callback.
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

        // TODO: confirm the exact controller/method name in your SDK.
        // In m5gedge variant this was DeviceManagementController.uploadDeviceIdentifier(body).
        // Your com.verizon.thingspace SDK may expose a different method name.
        // Once confirmed, replace the placeholder below.
        //
        // ApiResponse<?> response = verizonClient.getDeviceManagementController().uploadDeviceIdentifier(body);

        log.warn("checkOrderStatus wired to build the correct request body, " +
                "but the exact SDK method call needs confirmation in the thingspace package.");

        return Map.of(
                "status", "request body built — awaiting SDK method confirmation",
                "accountName", req.accountName()
        );
    }
}
