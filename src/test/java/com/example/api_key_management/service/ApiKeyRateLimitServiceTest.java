package com.example.api_key_management.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiKeyRateLimitServiceTest {

    @Test
    void shouldAllowFirstTenRequest(){
        ApiKeyRateLimitService apiKeyRateLimitService=new ApiKeyRateLimitService();

        for(int i=1;i<=10;i++){
            assertTrue(apiKeyRateLimitService.allowRequest(5L));
        }
    }

    @Test
    void shouldRejectEleventhRequest(){
        ApiKeyRateLimitService apiKeyRateLimitService=new ApiKeyRateLimitService();

        for(int i=1;i<=10;i++){
            assertTrue(apiKeyRateLimitService.allowRequest(4L));
        }
        assertFalse(apiKeyRateLimitService.allowRequest(4L));
    }

    @Test
    void shouldMaintainIndependentLimitsForDifferentApiKeys(){
        ApiKeyRateLimitService apiKeyRateLimitService=new ApiKeyRateLimitService();
        for (int i=1;i<=10;i++){
            assertTrue(apiKeyRateLimitService.allowRequest(1L));
        }

        for (int i=1;i<=5;i++){
            assertTrue(apiKeyRateLimitService.allowRequest(2L));
        }
        assertFalse(apiKeyRateLimitService.allowRequest(1l));
        assertTrue(apiKeyRateLimitService.allowRequest(2L));
    }

    @Test
    void shouldAllowRequestsAfterWindowReset() {
        Instant start = Instant.parse("2026-10-24T10:00:00Z");
        Clock clock = Clock.fixed(start, ZoneOffset.UTC);

        ApiKeyRateLimitService service = new ApiKeyRateLimitService(clock);

        for (int i = 1; i <= 10; i++) {
            assertTrue(service.allowRequest(1L));
        }

        assertFalse(service.allowRequest(1L));
        Clock advancedClock = Clock.offset(clock, java.time.Duration.ofSeconds(61));

        ApiKeyRateLimitService serviceAfterReset = new ApiKeyRateLimitService(advancedClock);

        assertTrue(serviceAfterReset.allowRequest(1L));
    }
}
