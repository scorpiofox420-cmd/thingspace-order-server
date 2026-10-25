package com.thingspace.order.controller;

import com.thingspace.order.dto.OrderRequest;
import com.thingspace.order.service.FimIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private FimIntegrationService fimService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitOrder(@RequestBody OrderRequest order) {
        System.out.println("Processing order: " + order.getWorkOrderNumber());
        String verizonResponse = fimService.sendOrderToVerizon(order);
        return ResponseEntity.ok("Order sent to Verizon. Response: " + verizonResponse);
    }
}
