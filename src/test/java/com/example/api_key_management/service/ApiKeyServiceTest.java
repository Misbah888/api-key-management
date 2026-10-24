package com.example.api_key_management.service;

import com.example.api_key_management.dto.ApiKeyStatsResponse;
import com.example.api_key_management.entity.ApiKey;
import com.example.api_key_management.repository.ApiKeyRepository;
import com.example.api_key_management.repository.ApiKeyUsageRepository;
import com.example.api_key_management.security.ApiKeyHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ApiKeyUsageRepository apiKeyUsageRepository;

    @Mock
    private ApiKeyHasher apiKeyHasher;

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    void shouldReturnCorrectApiKeyStats() {
        ApiKey apiKey = new ApiKey();
        apiKey.setId(1L);
        apiKey.setOwner("Developer 1");
        apiKey.setDailyLimit(10);
        when(apiKeyRepository.findById(1L)).thenReturn(Optional.of(apiKey));

        when(apiKeyUsageRepository.countTodayUsage(eq(1L),any(LocalDateTime.class)))
                .thenReturn(4L);

        when(apiKeyUsageRepository.countTotalUsage(1L)).thenReturn(20L);

        ApiKeyStatsResponse result=apiKeyService.getApiKeyStats(1L);

        assertEquals(1L,result.getApiKeyId());
        assertEquals(20L,result.getTotalRequests());
        assertEquals(4L,result.getRequestsToday());
    }

    @Test
    void shouldReturnStatsWhenDeveloperOwnsApiKey() {

        ApiKey apiKey = new ApiKey();
        apiKey.setId(1L);
        apiKey.setOwner("developer1");
        apiKey.setDailyLimit(10);

        when(apiKeyRepository.findById(1L))
                .thenReturn(Optional.of(apiKey));

        when(apiKeyUsageRepository.countTotalUsage(1L))
                .thenReturn(20L);

        when(apiKeyUsageRepository.countTodayUsage(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(4L);

        ApiKeyStatsResponse result =
                apiKeyService.getDeveloperApiKeyStats(1L, "developer1");

        assertEquals(1L, result.getApiKeyId());
        assertEquals("developer1", result.getOwner());
        assertEquals(20L, result.getTotalRequests());
        assertEquals(4L, result.getRequestsToday());
        assertEquals(6L, result.getRemainingRequests());
    }
    @Test
    void shouldReturnCorrectApiKeySTats() {

        ApiKey apiKey = new ApiKey();
        apiKey.setOwner("Developer 1");
        apiKey.setDailyLimit(10);

        when(apiKeyRepository.findById(1L))
                .thenReturn(Optional.of(apiKey));

        when(apiKeyUsageRepository.countTotalUsage(1L))
                .thenReturn(20L);

        when(apiKeyUsageRepository.countTodayUsage(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(4L);

        ApiKeyStatsResponse result =
                apiKeyService.getApiKeyStats(1L);

        assertEquals(20L, result.getTotalRequests());
        assertEquals(4L, result.getRequestsToday());
        assertEquals(6L, result.getRemainingRequests());
    }

    @Test
    void shouldReturnZeroRemainingRequestWhenLimitExceed(){
        ApiKey apiKey=new ApiKey();
        apiKey.setOwner("Developer 1");
        apiKey.setDailyLimit(3);
        when(apiKeyRepository.findById(1L))
                .thenReturn(Optional.of(apiKey));

        when(apiKeyUsageRepository.countTotalUsage(1L))
                .thenReturn(20L);

        when(apiKeyUsageRepository.countTodayUsage(eq(1L),any(LocalDateTime.class)))
                .thenReturn(3L);

        ApiKeyStatsResponse result=apiKeyService.getApiKeyStats(1L);

        assertEquals(0L,result.getRemainingRequests());
    }

    @Test
    void shouldThrowExceptionWhenDeveloperAccessesAnotherDevelopersApiKey(){
        ApiKey apiKey=new ApiKey();
        apiKey.setOwner("Developer 2");

        when(apiKeyRepository.findById(1L))
                .thenReturn(Optional.of(apiKey));

        assertThrows(ResponseStatusException.class,()-> apiKeyService.getDeveloperApiKeyStats(
                1L, "developer 1"));
    }

    @Test
    void shouldThrowExceptionWhenDeveloperApiKeyNotFound() {

        when(apiKeyRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> apiKeyService.getDeveloperApiKeyStats(99L, "developer1"));
    }

    @Test
    void shouldReturnZeroRemainingRequestsWhenUsageExceedsLimit(){
        ApiKey apiKey=new ApiKey();
        apiKey.setDailyLimit(5);
        when(apiKeyRepository.findById(1L))
                .thenReturn(Optional.of(apiKey));

        when(apiKeyUsageRepository.countTotalUsage(1L))
                        .thenReturn(50L);

        when(apiKeyUsageRepository.countTodayUsage(eq(1L),any(LocalDateTime.class)))
                .thenReturn(8L);

        ApiKeyStatsResponse result=apiKeyService.getApiKeyStats(1L);
        assertEquals(0L,result.getRemainingRequests());
    }

    @Test
    void shouldRevokeApiKey(){
        ApiKey apiKey=new ApiKey();
        apiKey.setOwner("Developer 1");
        apiKey.setActive(true);
        when(apiKeyRepository.findById(1L))
                .thenReturn(Optional.of(apiKey));
        apiKeyService.revokeApiKey(1L);

        assertFalse(apiKey.isActive());
    }

    @Test
    void shouldThrowExceptionWhenApiKeyNotFound() {

        when(apiKeyRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> apiKeyService.getApiKeyStats(99L));

    }
}