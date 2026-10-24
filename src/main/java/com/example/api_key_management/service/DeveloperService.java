package com.example.api_key_management.service;

import com.example.api_key_management.entity.Developer;
import com.example.api_key_management.repository.DeveloperRepository;
import com.example.api_key_management.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public DeveloperService(DeveloperRepository developerRepository,
            PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.developerRepository = developerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
    }

    public Developer registerDeveloper(
            String username,
            String email,
            String password) {

        if (developerRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (developerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        Developer developer = new Developer();
        developer.setUsername(username);
        developer.setEmail(email);
        developer.setPassword(passwordEncoder.encode(password));

        return developerRepository.save(developer);
    }

    public String loginDeveloper(String username, String password) {

        Developer developer = developerRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, developer.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return jwtService.generateToken(
                developer.getUsername(),
                "ROLE_DEVELOPER"
        );
    }
}
