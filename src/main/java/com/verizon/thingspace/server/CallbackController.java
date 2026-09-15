package com.verizon.thingspace.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Receives the async callback ThingSpace sends after a Check Order Status
 * request. Per Verizon's docs, that endpoint doesn't return status directly —
 * it triggers a callback POST to a URL you register separately (via
 * ConnectivityCallbacksController, most likely — not yet confirmed against
 * real source).
 *
 * This just captures the raw payload for now. Once you've seen a real
 * callback land (check /api/callbacks below, or the DB directly), we can
 * replace the raw-JSON storage with a proper parsed model matching the
 * actual field names ThingSpace sends.
 *
 * IMPORTANT: this URL must be publicly reachable for ThingSpace to call it —
 * e.g. https://your-app.onrender.com/api/callbacks/device-service — and that
 * exact URL needs to be registered with ThingSpace before callbacks will arrive.
 */
@RestController
@RequestMapping("/api/callbacks")
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    private final CallbackRepository callbackRepository;

    public CallbackController(CallbackRepository callbackRepository) {
        this.callbackRepository = callbackRepository;
    }

    @PostMapping("/device-service")
    public Map<String, String> receiveDeviceServiceCallback(@RequestBody String rawBody) {
        log.info("Received DeviceService callback: {}", rawBody);
        callbackRepository.save(new CallbackRecord(rawBody));
        return Map.of("status", "received");
    }

    @org.springframework.web.bind.annotation.GetMapping
    public List<CallbackRecord> allCallbacks() {
        return callbackRepository.findAll();
    }
}
