package com.verizon.thingspace.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.verizon.thingspace.VerizonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderSubmissionController {

    private final VerizonClient client;

    public OrderSubmissionController(VerizonClient client) {
        this.client = client;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitOrder(@RequestBody OrderRequest order) throws Exception {
        System.out.println("Processing order PO: " + order.getWorkOrderNumber());

        // Build Verizon ThingSpace payload dynamically
        ObjectNode verizonPayload = client.mapper().createObjectNode();
        verizonPayload.put("accountName", order.getAccountNumber());
        verizonPayload.put("customerPoNumber", order.getCustomerPoNumber());

        var deviceIdsArray = verizonPayload.putArray("deviceIds");

        if (order.getImei() != null && !order.getImei().isEmpty()) {
            deviceIdsArray.addObject().put("id", order.getImei()).put("kind", "IMEI");
        }
        if (order.getIccid() != null && !order.getIccid().isEmpty()) {
            deviceIdsArray.addObject().put("id", order.getIccid()).put("kind", "ICCID");
        }
        if (order.getWirelessNumber() != null && !order.getWirelessNumber().isEmpty()) {
            deviceIdsArray.addObject().put("id", order.getWirelessNumber()).put("kind", "MDN");
        }

        // Send to Verizon API endpoint
        String url = client.baseUrl() + "/m2m/v1/devices/actions/activate";
        VerizonClient.HttpResult result = client.request("POST", url, verizonPayload);

        if (!result.isOk()) {
            return ResponseEntity.status(result.statusCode())
                    .body("Verizon API Error: " + result.json().toString());
        }

        return ResponseEntity.ok("Order " + order.getWorkOrderNumber() + " successfully submitted to Verizon ThingSpace!");
    }
}
