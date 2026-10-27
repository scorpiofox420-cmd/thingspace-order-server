package com.verizon.thingspace.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @PostMapping("/order/echo")
    public ResponseEntity<String> echoOrder(@RequestBody OrderRequest order) {
        System.out.println("Received echo for order: " + order.getWorkOrderNumber());
        return ResponseEntity.ok("Echo: Order " + order.getWorkOrderNumber() + " received successfully.");
    }
}
