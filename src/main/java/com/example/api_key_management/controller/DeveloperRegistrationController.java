package com.example.api_key_management.controller;

import com.example.api_key_management.dto.*;
import com.example.api_key_management.entity.Developer;
import com.example.api_key_management.service.ApiKeyService;
import com.example.api_key_management.service.DeveloperService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/developers")
public class DeveloperRegistrationController {

    private final DeveloperService developerService;
    private final ApiKeyService apiKeyService;

    public DeveloperRegistrationController(DeveloperService developerService,ApiKeyService apiKeyService) {
        this.developerService = developerService;
        this.apiKeyService=apiKeyService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerDeveloper(
            @Valid @RequestBody DeveloperRegistrationRequest request) {

        Developer developer = developerService.registerDeveloper(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        Map<String, Object> response = Map.of(
                "id", developer.getId(),
                "username", developer.getUsername(),
                "email", developer.getEmail(),
                "message", "Developer registered successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginDeveloper(
            @Valid @RequestBody DeveloperLoginRequest request) {

        String token = developerService.loginDeveloper(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of("token", token, "message", "Login successful")
        );
    }

    @GetMapping("/keys")
    public ResponseEntity<List<ApiKeySummaryResponse>> getMyApiKeys(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                apiKeyService.getDeveloperKeys(userDetails.getUsername())
        );
    }

    @PostMapping("/keys")
    public ResponseEntity<CreateApiKeyResponse> createMyApiKey(
            @AuthenticationPrincipal UserDetails userDetails) {

        String owner = userDetails.getUsername();

        String rawKey = apiKeyService.generateApiKey(owner);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateApiKeyResponse(owner, rawKey));
    }

    @GetMapping("/keys/{id}/stats")
    public ResponseEntity<ApiKeyStatsResponse> getMyApiKeyStats(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        ApiKeyStatsResponse stats = apiKeyService.getDeveloperApiKeyStats(
                id, userDetails.getUsername());

        return ResponseEntity.ok(stats);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateRegistration(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}