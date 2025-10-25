package com.auth.Auth.Service.repository;

import com.auth.Auth.Service.entity.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, UUID> {
    Optional<Integration> findByName(String name);
}
