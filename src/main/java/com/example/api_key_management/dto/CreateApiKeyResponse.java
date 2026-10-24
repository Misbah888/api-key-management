package com.example.api_key_management.dto;

public class CreateApiKeyResponse {

    private String owner;
    private String apiKey;

    public CreateApiKeyResponse() {
    }

    public CreateApiKeyResponse(String owner, String apiKey) {
        this.owner = owner;
        this.apiKey = apiKey;
    }

    public String getOwner() {
        return owner;
    }

    public String getApiKey() {
        return apiKey;
    }
}