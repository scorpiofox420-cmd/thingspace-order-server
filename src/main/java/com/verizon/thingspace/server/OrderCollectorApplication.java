package com.verizon.thingspace.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point. Starts a web server (REST API) and a background scheduler
 * that polls ThingSpace for order status on a fixed interval.
 */
@SpringBootApplication
@EnableScheduling
public class OrderCollectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderCollectorApplication.class, args);
    }
}
