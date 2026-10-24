package com.example.api_key_management.service;

import com.example.api_key_management.entity.ApiKey;
import com.example.api_key_management.entity.ApiKeyUsage;
import com.example.api_key_management.repository.ApiKeyUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiKeyUsageService {

    private final ApiKeyUsageRepository apiKeyUsageRepository;
    public ApiKeyUsageService(
            ApiKeyUsageRepository apiKeyUsageRepository) {
        this.apiKeyUsageRepository = apiKeyUsageRepository;
    }

    @Transactional
    public void recordUsage(ApiKey apiKey, String endpoint) {
        ApiKeyUsage usage = new ApiKeyUsage();

        usage.setApiKey(apiKey);
        usage.setEndpoint(endpoint);

        apiKeyUsageRepository.save(usage);
    }
}