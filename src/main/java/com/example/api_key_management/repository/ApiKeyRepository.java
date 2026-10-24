package com.example.api_key_management.repository;

import com.example.api_key_management.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey,Long> {
    Optional<ApiKey> findByKeyHash(String keyHash);
    List<ApiKey> findByOwner(String owner);
}
