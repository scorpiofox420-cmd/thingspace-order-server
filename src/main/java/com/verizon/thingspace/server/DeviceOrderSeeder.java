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

    public DeviceOrderSeeder(DeviceOrderRepository deviceOrderRepository,
                             OrderCustomerInfoRepository customerInfoRepository) {
        this.deviceOrderRepository = deviceOrderRepository;
        this.customerInfoRepository = customerInfoRepository;
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

        // Delivery methods:
        //   PICKUP = in-store pickup at Verizon S Westnedge Portage MI
        //   SHIP   = shipped to address
        //   NEXT_DAY = next-day delivery (in-store not in stock)

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
        log.info("Seeded customer info and {} device-order line items ({} total devices).",
                items.size(), items.stream().mapToInt(DeviceOrderItem::getQuantity).sum());
    }
}
