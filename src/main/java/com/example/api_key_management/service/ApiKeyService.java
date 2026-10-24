package com.example.api_key_management.service;

import com.example.api_key_management.dto.ApiKeyStatsResponse;
import com.example.api_key_management.dto.ApiKeySummaryResponse;
import com.example.api_key_management.entity.ApiKey;
import com.example.api_key_management.repository.ApiKeyRepository;
import com.example.api_key_management.repository.ApiKeyUsageRepository;
import com.example.api_key_management.security.ApiKeyHasher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyHasher apiKeyHasher;
    private final SecureRandom secureRandom = new SecureRandom();
    private final ApiKeyUsageRepository apiKeyUsageRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository, ApiKeyHasher apiKeyHasher,
            ApiKeyUsageRepository apiKeyUsageRepository) {

        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyHasher = apiKeyHasher;
        this.apiKeyUsageRepository = apiKeyUsageRepository;
    }

    @Transactional
    public String generateApiKey(String owner) {
        //Generates 32 cryptographically secure random bytes.
        byte[] randomBytes = new byte[32];
        //Fills the byte array with random bytes.
        secureRandom.nextBytes(randomBytes);

        String rawKey = "ak_live_" +
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(randomBytes);

        ApiKey apiKey = new ApiKey();
        apiKey.setOwner(owner);
        apiKey.setKeyHash(apiKeyHasher.hash(rawKey));
        apiKeyRepository.save(apiKey);
        return rawKey;
    }

    @Transactional(readOnly = true)
    public List<ApiKeySummaryResponse> getAllApiKeys() {

        return apiKeyRepository.findAll()
                .stream()
                .map(apiKey -> new ApiKeySummaryResponse(
                        apiKey.getId(),
                        apiKey.getOwner(),
                        apiKey.isActive(),
                        apiKey.getDailyLimit(),
                        apiKey.getExpiresAt(),
                        apiKey.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void revokeApiKey(Long id) {

        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "API key not found"
                ));
        apiKey.setActive(false);

        apiKeyRepository.save(apiKey);
    }

    public ApiKeyStatsResponse getApiKeyStats(Long id) {

        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "API key not found"));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        long totalRequests = apiKeyUsageRepository.countTotalUsage(id);

        long requestsToday = apiKeyUsageRepository.countTodayUsage(id, startOfDay);

        long remainingRequests = Math.max( 0L, apiKey.getDailyLimit() - requestsToday);

        return new ApiKeyStatsResponse(
                apiKey.getId(),
                apiKey.getOwner(),
                totalRequests,
                requestsToday,
                apiKey.getDailyLimit(),
                remainingRequests
        );
    }

    public ApiKeyStatsResponse getDeveloperApiKeyStats(Long id, String owner) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .filter(key -> key.getOwner().equals(owner))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "API key not found"));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long totalRequests = apiKeyUsageRepository.countTotalUsage(id);
        long requestsToday = apiKeyUsageRepository.countTodayUsage(id, startOfDay);
        long remainingRequests = Math.max(
                0L, apiKey.getDailyLimit() - requestsToday);

        return new ApiKeyStatsResponse(
                apiKey.getId(),
                apiKey.getOwner(),
                totalRequests,
                requestsToday,
                apiKey.getDailyLimit(),
                remainingRequests
        );
    }
    @Transactional(readOnly = true)
    public List<ApiKeySummaryResponse> getDeveloperKeys(String username) {

        return apiKeyRepository.findByOwner(username)
                .stream()
                .map(apiKey -> new ApiKeySummaryResponse(
                        apiKey.getId(),
                        apiKey.getOwner(),
                        apiKey.isActive(),
                        apiKey.getDailyLimit(),
                        apiKey.getExpiresAt(),
                        apiKey.getCreatedAt()
                ))
                .toList();
    }
}
