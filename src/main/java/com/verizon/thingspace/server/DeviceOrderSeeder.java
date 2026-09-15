package com.verizon.thingspace.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceOrderSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DeviceOrderSeeder.class);

    private final DeviceOrderRepository deviceOrderRepository;
    private final OrderCustomerInfoRepository customerInfoRepository;
    private final DeviceDetailRepository deviceDetailRepository;

    public DeviceOrderSeeder(DeviceOrderRepository deviceOrderRepository,
                             OrderCustomerInfoRepository customerInfoRepository,
                             DeviceDetailRepository deviceDetailRepository) {
        this.deviceOrderRepository = deviceOrderRepository;
        this.customerInfoRepository = customerInfoRepository;
        this.deviceDetailRepository = deviceDetailRepository;
    }

    @Override
    public void run(String... args) {
        if (deviceOrderRepository.count() > 0) {
            log.info("Device orders already seeded — skipping.");
            return;
        }

        // --- Customer info ---
        customerInfoRepository.save(new OrderCustomerInfo(
                "Makayla Fox",
                "3489 Comstock Village Ln",
                "Apt 103",
                "Kalamazoo",
                "MI",
                null,
                "scorpiofox420@gmail.com",
                "269-248-3433",
                "Verizon — S Westnedge, Portage, MI 49002"
        ));

        // --- Line items ---
        String huluDisneyDiscovery = "Hulu,Disney+,Discovery+";
        String appleMusic = "Apple Music";
        String youtubePremium = "YouTube Premium";

        List<DeviceOrderItem> items = List.of(
                new DeviceOrderItem("Samsung Galaxy S26 Ultra", "Black",    "1TB",  3,
                        huluDisneyDiscovery + "," + youtubePremium, true, true, true, true,
                        "PICKUP"),
                new DeviceOrderItem("Samsung Galaxy S26 Ultra", "Violet",   "512GB", 1,
                        huluDisneyDiscovery + "," + youtubePremium, true, true, true, true,
                        "PICKUP"),
                new DeviceOrderItem("iPhone 18 Pro Max",        "Glacier",  "2TB",  3,
                        huluDisneyDiscovery + "," + appleMusic,     true, true, true, true,
                        "SHIP"),
                new DeviceOrderItem("iPhone 17 Pro Max",        "Blue",     "2TB",  2,
                        huluDisneyDiscovery + "," + appleMusic,     true, true, true, true,
                        "PICKUP"),
                new DeviceOrderItem("iPhone 17 Pro Max",        "Silver",   "2TB",  2,
                        huluDisneyDiscovery + "," + appleMusic,     true, true, true, true,
                        "PICKUP"),
                new DeviceOrderItem("Google Pixel 11 Pro XL",   "Fog",      "512GB", 3,
                        huluDisneyDiscovery + "," + youtubePremium, true, true, true, true,
                        "NEXT_DAY")
        );

        deviceOrderRepository.saveAll(items);

        // --- Device details: phone number transfers ---
        // iPhone 18 Pro Max line 1 of 3: number 269-248-3433 (Makayla's current number) transferred successfully
        deviceDetailRepository.save(new DeviceDetail(
                "iPhone 18 Pro Max", "Glacier", "2TB", 1,
                null, "269-248-3433", "SUCCESSFUL", "Previous Carrier"));

        // iPhone 17 Pro Max Silver line 1 of 2: number 269-548-9133 transferred successfully from other carrier
        deviceDetailRepository.save(new DeviceDetail(
                "iPhone 17 Pro Max", "Silver", "2TB", 1,
                null, "269-548-9133", "SUCCESSFUL", "Previous Carrier"));

        log.info("Seeded customer info, {} device-order line items ({} total devices), and 2 device details with phone transfers.",
                items.size(), items.stream().mapToInt(DeviceOrderItem::getQuantity).sum());
    }
}
