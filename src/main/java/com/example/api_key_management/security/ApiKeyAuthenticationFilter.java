package com.example.api_key_management.security;

import com.example.api_key_management.entity.ApiKey;
import com.example.api_key_management.repository.ApiKeyRepository;
import com.example.api_key_management.service.ApiKeyQuotaService;
import com.example.api_key_management.service.ApiKeyRateLimitService;
import com.example.api_key_management.service.ApiKeyUsageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyHasher apiKeyHasher;
    private final ApiKeyUsageService apiKeyUsageService;
    private final ApiKeyQuotaService apiKeyQuotaService;
    private final ApiKeyRateLimitService apiKeyRateLimitService;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository,ApiKeyQuotaService apiKeyQuotaService,
                                      ApiKeyHasher apiKeyHasher,ApiKeyUsageService apiKeyUsageService,ApiKeyRateLimitService apiKeyRateLimitService) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyHasher = apiKeyHasher;
        this.apiKeyUsageService = apiKeyUsageService;
        this.apiKeyQuotaService=apiKeyQuotaService;
        this.apiKeyRateLimitService=apiKeyRateLimitService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/api/demo/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String rawKey = request.getHeader("X-API-Key");

        if (rawKey == null || rawKey.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing API key"
            );
            return;
        }

        String keyHash = apiKeyHasher.hash(rawKey);

        ApiKey apiKey = apiKeyRepository.findByKeyHash(keyHash)
                .orElse(null);

        if (apiKey == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid API key"
            );
            return;
        }

        if (!apiKey.isActive()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "API key has been revoked"
            );
            return;
        }

        if (apiKey.getExpiresAt() != null  && !apiKey.getExpiresAt().isAfter(LocalDateTime.now())) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "API key has expired");
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        apiKey.getOwner(),
                        null,
                        List.of());

        if (!apiKeyQuotaService.hasQuota(apiKey)) {
            response.sendError(
                    429,
                    "Daily API key quota exceeded"
            );
            return;
        }

        if (!apiKeyRateLimitService.allowRequest(apiKey.getId())) {
            response.sendError(
                    429,
                    "Rate limit exceeded. Maximum 10 requests per minute."
            );
            return;
        }

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        apiKeyUsageService.recordUsage(
                apiKey,
                request.getRequestURI()
        );

        filterChain.doFilter(request, response);
    }
}