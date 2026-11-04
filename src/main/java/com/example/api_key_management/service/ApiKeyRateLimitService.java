package com.example.api_key_management.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Clock;

@Service
public class ApiKeyRateLimitService {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MILLIS = 60_000;
    private final Clock clock;

    private final ConcurrentHashMap<Long, RateLimitCounter> requestCounts =
            new ConcurrentHashMap<>();
    public ApiKeyRateLimitService() {
        this(Clock.systemUTC());
    }
    ApiKeyRateLimitService(Clock clock) {
        this.clock = clock;
    }

    public boolean allowRequest(Long apiKeyId) {

        long currentTime = clock.millis();

        RateLimitCounter counter = requestCounts.compute(
                apiKeyId,
                (id, oldCounter) -> {
                    if (oldCounter == null ||
                            currentTime - oldCounter.windowStart >= WINDOW_MILLIS) {
                        return new RateLimitCounter(currentTime);
                    }
                    return oldCounter;
                }
        );

        int requests = counter.requestCount.incrementAndGet();

        if (requests <= MAX_REQUESTS) {
            return true;
        }

        return false;
    }

    private static class RateLimitCounter {
        private final long windowStart;
        private final AtomicInteger requestCount = new AtomicInteger(0);

        private RateLimitCounter(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
