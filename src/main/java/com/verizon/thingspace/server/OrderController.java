package com.thingspace.order.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thingspace.order.dto.OrderRequest;
import com.verizon.thingspace.VerizonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final VerizonClient client;

    public OrderController(VerizonClient client) {
        this.client = client;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitOrder(@RequestBody OrderRequest order) throws Exception {
        System.out.println("Processing order for Account: " + order.getAccountNumber());
        System.out.println("Wireless Number: " + order.getWirelessNumber());

        // 1. Build the payload mapped to Verizon's expected structure
        ObjectNode verizonPayload = client.mapper().createObjectNode();
        verizonPayload.put("accountName", order.getAccountNumber());
        verizonPayload.put("customerPoNumber", order.getCustomerPoNumber());
        
        // Add device identifiers array if wireless number is present
        if (order.getWirelessNumber() != null) {
            var deviceNode = verizonPayload.putArray("deviceIds");
            deviceNode.addObject()
                      .put("id", order.getWirelessNumber())
                      .put("kind", "MDN");
        }

        // 2. Route to Verizon ThingSpace Activation / Device Endpoint
        String url = client.baseUrl() + "/m2m/v1/devices/actions/activate";
        VerizonClient.HttpResult result = client.request("POST", url, verizonPayload);
        
        if (!result.isOk()) {
            return ResponseEntity.status(result.statusCode())
                    .body("Failed to reach Verizon: " + result.json().toString());
        }

        return ResponseEntity.ok("Order " + order.getWorkOrderNumber() + 
                                 " successfully submitted to Verizon ThingSpace!");
    }
}
