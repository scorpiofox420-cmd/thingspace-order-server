package com.thingspace.order.controller;

import com.thingspace.order.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping("/submit")
    public ResponseEntity<String> submitOrder(@RequestBody OrderRequest order) {
        System.out.println("Received order: " + order.getWorkOrderNumber());
        return ResponseEntity.ok("Order " + order.getWorkOrderNumber() + " successfully entered!");
    }
}
