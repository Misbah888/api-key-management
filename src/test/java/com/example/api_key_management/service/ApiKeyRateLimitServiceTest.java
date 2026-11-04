package com.example.api_key_management.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
    void shouldAllowRequestsAfterWindowReset() {
        Instant start = Instant.parse("2026-10-24T10:00:00Z");

        AdjustableClock clock = new AdjustableClock(start);

        ApiKeyRateLimitService service =
                new ApiKeyRateLimitService(clock);

        for (int i = 1; i <= 10; i++) {
            assertTrue(service.allowRequest(1L));
        }

        assertFalse(service.allowRequest(1L));

        clock.advanceSeconds(61);

        assertTrue(service.allowRequest(1L));
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


    private static class AdjustableClock extends Clock {

        private Instant currentTime;

        AdjustableClock(Instant initialTime) {
            this.currentTime = initialTime;
        }

        public void advanceSeconds(long seconds) {
            currentTime = currentTime.plusSeconds(seconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return currentTime;
        }
    }
}
