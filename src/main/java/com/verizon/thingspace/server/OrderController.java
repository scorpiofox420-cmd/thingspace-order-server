package com.verizon.thingspace.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Read-only view of collected request/order status, plus a manual trigger for testing.
 *
 *   GET  /api/orders             -> all collected records
 *   GET  /api/orders/{requestId} -> record for one request ID
 *   POST /api/orders/poll-now    -> trigger an immediate poll cycle (for testing)
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderPollingService pollingService;

    public OrderController(OrderRepository orderRepository, OrderPollingService pollingService) {
        this.orderRepository = orderRepository;
        this.pollingService = pollingService;
    }

    @GetMapping
    public List<OrderRecord> allOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{requestId}")
    public OrderRecord orderForRequest(@PathVariable String requestId) {
        return orderRepository.findByRequestId(requestId).orElse(null);
    }

    @PostMapping("/poll-now")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> pollNow() {
        pollingService.pollAllDevices();
        return Map.of("status", "poll cycle triggered");
    }

    /**
     * Stores an order status record from pasted order data (e.g. an order
     * confirmation / handshake payload) so it is served back via GET /api/orders.
     */
    @PostMapping("/intake")
    public OrderRecord intake(@RequestBody Map<String, Object> body) {
        ObjectMapper mapper = new ObjectMapper();
        String requestId = stringOf(body.get("orderNumber"), body.get("requestId"), null);
        String accountName = stringOf(body.get("customerName"), body.get("accountName"), "");
        String status = stringOf(body.get("status"), "UNKNOWN");
        String rawBody = toJson(mapper, body);
        OrderRecord record = orderRepository.findByRequestId(requestId)
                .orElseGet(() -> orderRepository.save(new OrderRecord(requestId, accountName, status, rawBody)));
        record.setStatus(status);
        record.setRawResponse(rawBody);
        return orderRepository.save(record);
    }

    private static String stringOf(Object value, String fallback) {
        return value == null || value.toString().isEmpty() ? fallback : value.toString();
    }

    private static String toJson(ObjectMapper mapper, Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            return value.toString();
        }
    }

    private static String stringOf(Object a, Object b, String fallback) {
        String v = stringOf(a, null);
        return v != null ? v : stringOf(b, fallback);
    }
}
