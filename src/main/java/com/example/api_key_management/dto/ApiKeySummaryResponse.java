package com.example.api_key_management.dto;
import java.time.LocalDateTime;

public class ApiKeySummaryResponse {
    private Long id;
    private String owner;
    private boolean active;
    private Integer dailyLimit;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public ApiKeySummaryResponse(
            Long id,
            String owner,
            boolean active,
            Integer dailyLimit,
            LocalDateTime expiresAt,
            LocalDateTime createdAt) {

        this.id = id;
        this.owner = owner;
        this.active = active;
        this.dailyLimit = dailyLimit;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}