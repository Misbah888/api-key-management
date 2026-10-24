package com.example.api_key_management.dto;

public class ApiKeyStatsResponse {

    private Long apiKeyId;
    private String owner;
    private long totalRequests;
    private long requestsToday;
    private Integer dailyLimit;
    private long remainingRequests;

    public ApiKeyStatsResponse(
            Long apiKeyId,
            String owner,
            long totalRequests,
            long requestsToday,
            Integer dailyLimit,
            long remainingRequests) {

        this.apiKeyId = apiKeyId;
        this.owner = owner;
        this.totalRequests = totalRequests;
        this.requestsToday = requestsToday;
        this.dailyLimit = dailyLimit;
        this.remainingRequests = remainingRequests;
    }

    public Long getApiKeyId() {
        return apiKeyId;
    }

    public String getOwner() {
        return owner;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getRequestsToday() {
        return requestsToday;
    }

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public long getRemainingRequests() {
        return remainingRequests;
    }
}