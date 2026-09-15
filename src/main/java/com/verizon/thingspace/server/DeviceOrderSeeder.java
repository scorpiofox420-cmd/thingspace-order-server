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

    public DeviceOrderSeeder(DeviceOrderRepository deviceOrderRepository) {
        this.deviceOrderRepository = deviceOrderRepository;
    }

    @Override
    public void run(String... args) {
        if (deviceOrderRepository.count() > 0) {
            log.info("Device orders already seeded — skipping.");
            return;
        }

        String huluDisneyDiscovery = "Hulu,Disney+,Discovery+";
        String appleMusic = "Apple Music";
        String youtubePremium = "YouTube Premium";

        List<DeviceOrderItem> items = List.of(
                new DeviceOrderItem("Samsung Galaxy S26 Ultra", "Black",    "1TB",  3,
                        huluDisneyDiscovery + "," + youtubePremium, true, true, true, true),
                new DeviceOrderItem("Samsung Galaxy S26 Ultra", "Violet",   "512GB", 1,
                        huluDisneyDiscovery + "," + youtubePremium, true, true, true, true),
                new DeviceOrderItem("iPhone 18 Pro Max",        "Glacier",  "2TB",  3,
                        huluDisneyDiscovery + "," + appleMusic,     true, true, true, true),
                new DeviceOrderItem("iPhone 17 Pro Max",        "Blue",     "2TB",  2,
                        huluDisneyDiscovery + "," + appleMusic,     true, true, true, true),
                new DeviceOrderItem("iPhone 17 Pro Max",        "Silver",   "2TB",  2,
                        huluDisneyDiscovery + "," + appleMusic,     true, true, true, true),
                new DeviceOrderItem("Google Pixel 11 Pro XL",   "Fog",      "512GB", 3,
                        huluDisneyDiscovery + "," + youtubePremium, true, true, true, true)
        );

        deviceOrderRepository.saveAll(items);
        log.info("Seeded {} device-order line items ({} total devices).",
                items.size(), items.stream().mapToInt(DeviceOrderItem::getQuantity).sum());
    }
}
