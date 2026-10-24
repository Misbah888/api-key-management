package com.example.api_key_management.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ApiKeyRateLimitService {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MILLIS = 60_000;

    private final ConcurrentHashMap<Long, RateLimitCounter> requestCounts =
            new ConcurrentHashMap<>();

    public boolean allowRequest(Long apiKeyId) {

        long currentTime = System.currentTimeMillis();

        RateLimitCounter counter = requestCounts.compute(
                apiKeyId,
                (id, existingCounter) -> {

                    if (existingCounter == null
                                || currentTime - existingCounter.windowStart >= WINDOW_MILLIS) {
                            return new RateLimitCounter(currentTime);
                    }
                        return existingCounter;
                    }
                );

        int requests = counter.requestCount.incrementAndGet();

        return requests <= MAX_REQUESTS;
        }
    private static class RateLimitCounter {

        private final long windowStart;
        private final AtomicInteger requestCount = new AtomicInteger(0);
        //When we create a new counter, we provide its starting timestamp
        private RateLimitCounter(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}

