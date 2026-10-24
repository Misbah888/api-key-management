package com.example.api_key_management.service;

import com.example.api_key_management.entity.ApiKey;
import com.example.api_key_management.repository.ApiKeyUsageRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ApiKeyQuotaService {

    private final ApiKeyUsageRepository apiKeyUsageRepository;

    public ApiKeyQuotaService(
            ApiKeyUsageRepository apiKeyUsageRepository) {
        this.apiKeyUsageRepository = apiKeyUsageRepository;
    }

    public boolean hasQuota(ApiKey apiKey) {

        LocalDateTime startOfDay =
                LocalDate.now().atStartOfDay();

        long usedToday = apiKeyUsageRepository.countTodayUsage(
                apiKey.getId(),
                startOfDay
        );
        return usedToday < apiKey.getDailyLimit();
    }
}