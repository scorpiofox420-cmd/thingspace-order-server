package com.verizon.thingspace.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

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
}
