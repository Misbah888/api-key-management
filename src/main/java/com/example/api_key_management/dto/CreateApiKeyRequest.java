package com.example.api_key_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateApiKeyRequest {

    @NotBlank(message = "Owner is required")
    @Size(max = 100, message = "Owner must not exceed 100 characters")
    private String owner;

    public CreateApiKeyRequest() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}