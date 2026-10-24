package com.example.api_key_management.repository;

import com.example.api_key_management.entity.ApiKeyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ApiKeyUsageRepository  extends JpaRepository<ApiKeyUsage, Long> {
    @Query("""
            SELECT COUNT(u)
            FROM ApiKeyUsage u
            WHERE u.apiKey.id = :apiKeyId
              AND u.usedAt >= :startOfDay
            """)
    long countTodayUsage(
            @Param("apiKeyId") Long apiKeyId,
            @Param("startOfDay") LocalDateTime startOfDay
    );

    @Query("""
        SELECT COUNT(u)
        FROM ApiKeyUsage u
        WHERE u.apiKey.id = :apiKeyId
        """)
    long countTotalUsage(@Param("apiKeyId") Long apiKeyId);
}