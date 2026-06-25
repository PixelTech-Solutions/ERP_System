package com.pixeltech.erp.chaos;

import com.pixeltech.erp.common.BusinessException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Chaos / incident-simulation endpoints.
 *
 * These let us deliberately break the app on demand so the DevOps Commander
 * AI agents have real incidents to detect, investigate and (later) remediate.
 * In a real deployment this controller would be disabled in production, but
 * for this AI-103 demo it is the trigger for the whole incident workflow.
 */
@RestController
@RequestMapping("/api/chaos")
public class ChaosController {

    // When true, /api/chaos/maybe-fail starts throwing 500s — simulates a
    // service degradation that monitoring should catch.
    private static final AtomicBoolean failureMode = new AtomicBoolean(false);

    @PostMapping("/enable-failures")
    public Map<String, Object> enableFailures() {
        failureMode.set(true);
        return Map.of("failureMode", true, "message", "App will now return errors on /api/chaos/maybe-fail");
    }

    @PostMapping("/disable-failures")
    public Map<String, Object> disableFailures() {
        failureMode.set(false);
        return Map.of("failureMode", false, "message", "Recovered: errors disabled");
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("failureMode", failureMode.get());
    }

    /** Returns 500 while failure mode is on — the thing alerts fire on. */
    @GetMapping("/maybe-fail")
    public Map<String, Object> maybeFail() {
        if (failureMode.get()) {
            throw new RuntimeException("Simulated downstream failure (chaos mode is ON)");
        }
        return Map.of("status", "ok");
    }

    /** Blocks the request for the given milliseconds — simulates latency spikes. */
    @GetMapping("/slow")
    public Map<String, Object> slow(@RequestParam(defaultValue = "3000") long ms) {
        if (ms > 30000) {
            throw new BusinessException("Refusing to sleep more than 30s");
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Map.of("sleptMs", ms);
    }
}
