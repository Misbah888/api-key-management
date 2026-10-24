package com.example.api_key_management.repository;

import com.example.api_key_management.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    Optional<Developer> findByUsername(String username);

    Optional<Developer> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}