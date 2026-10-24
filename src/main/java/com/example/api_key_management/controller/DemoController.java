package com.example.api_key_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/api/demo/data")
    public Map<String, String> getDemoData() {
        return Map.of(
                "message", "API key authentication successful",
                "data", "This is protected information"
        );
    }
}