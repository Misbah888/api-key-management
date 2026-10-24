package com.example.api_key_management.controller;

import com.example.api_key_management.dto.ApiKeyStatsResponse;
import com.example.api_key_management.dto.ApiKeySummaryResponse;
import com.example.api_key_management.dto.CreateApiKeyRequest;
import com.example.api_key_management.dto.CreateApiKeyResponse;
import com.example.api_key_management.service.ApiKeyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateApiKeyResponse createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request) {

        String rawKey = apiKeyService.generateApiKey(request.getOwner());

        return new CreateApiKeyResponse(request.getOwner(), rawKey);
    }

    @GetMapping
    public List<ApiKeySummaryResponse> getAllApiKeys() {

        return apiKeyService.getAllApiKeys();
    }

    @PatchMapping("/{id}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeApiKey(@PathVariable Long id) {
        apiKeyService.revokeApiKey(id);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiKeyStatsResponse> getApiKeyStats(@PathVariable Long id) {

        ApiKeyStatsResponse stats = apiKeyService.getApiKeyStats(id);

        return ResponseEntity.ok(stats);
    }
}