package com.verizon.thingspace.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/device-orders")
public class DeviceOrderController {

    private static final Logger log = LoggerFactory.getLogger(DeviceOrderController.class);

    private final DeviceOrderRepository deviceOrderRepository;
    private final OrderCustomerInfoRepository customerInfoRepository;

    public DeviceOrderController(DeviceOrderRepository deviceOrderRepository,
                                 OrderCustomerInfoRepository customerInfoRepository) {
        this.deviceOrderRepository = deviceOrderRepository;
        this.customerInfoRepository = customerInfoRepository;
    }

    @GetMapping
    public List<DeviceOrderItem> allItems() {
        return deviceOrderRepository.findAll();
    }

    @GetMapping("/customer")
    public OrderCustomerInfo customerInfo() {
        return customerInfoRepository.findAll().stream().findFirst().orElse(null);
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        List<DeviceOrderItem> items = deviceOrderRepository.findAll();
        List<OrderCustomerInfo> customers = customerInfoRepository.findAll();

        int totalDevices = items.stream().mapToInt(DeviceOrderItem::getQuantity).sum();
        int totalLineItems = items.size();

        Map<String, Integer> perModel = new LinkedHashMap<>();
        Map<String, Integer> addonCounts = new LinkedHashMap<>();
        Map<String, Integer> deliveryCounts = new LinkedHashMap<>();

        for (DeviceOrderItem item : items) {
            String label = item.getColor() + " " + item.getModel() + " " + item.getStorage();
            perModel.put(label, item.getQuantity());

            deliveryCounts.merge(item.getDeliveryMethod(), item.getQuantity(), Integer::sum);

            if (item.getAddOns() != null) {
                Arrays.stream(item.getAddOns().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(addon -> addonCounts.merge(addon, item.getQuantity(), Integer::sum));
            }
        }

        boolean allPaidInFull = items.stream().allMatch(DeviceOrderItem::isPaidInFull);
        boolean allAutoPay = items.stream().allMatch(DeviceOrderItem::isAutoPay);
        boolean allFullCoverage = items.stream().allMatch(DeviceOrderItem::isFullCoverage);
        boolean allCreditVerified = items.stream().allMatch(DeviceOrderItem::isCreditCheckVerified);

        log.info("Device order summary: {} devices across {} line items", totalDevices, totalLineItems);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customer", customers.stream().findFirst().orElse(null));
        result.put("totalDevices", totalDevices);
        result.put("totalLineItems", totalLineItems);
        result.put("perModel", perModel);
        result.put("addOnCounts", addonCounts);
        result.put("deliveryCounts", deliveryCounts);
        result.put("allPaidInFull", allPaidInFull);
        result.put("allAutoPay", allAutoPay);
        result.put("allFullCoverage", allFullCoverage);
        result.put("allCreditCheckVerified", allCreditVerified);
        return result;
    }
}
