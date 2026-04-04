package com.example.finance_dashboard_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RateLimitService {
    private final int capacity;
    private final long windowSeconds;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public RateLimitService(
            @Value("${app.rate-limit.capacity}") int capacity,
            @Value("${app.rate-limit.window-seconds}") long windowSeconds) {
        this.capacity = capacity;
        this.windowSeconds = windowSeconds;
    }

    public boolean isAllowed(String key) {
        Instant now = Instant.now();
        WindowCounter counter = counters.compute(key, (ignored, existing) -> {
            if (existing == null || now.isAfter(existing.windowEnd())) {
                return new WindowCounter(new AtomicLong(1), now.plusSeconds(windowSeconds));
            }
            return existing.incrementAndGet();
        });
        return counter.count() <= capacity;
    }

    private record WindowCounter(AtomicLong counter, Instant windowEnd) {

        long count() {
            return counter.get();
        }

        WindowCounter incrementAndGet() {
            counter.incrementAndGet();
            return this;
        }
    }
}
