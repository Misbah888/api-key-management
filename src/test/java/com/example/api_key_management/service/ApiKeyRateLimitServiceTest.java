package com.example.api_key_management.service;

import org.junit.jupiter.api.Test;

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
}
