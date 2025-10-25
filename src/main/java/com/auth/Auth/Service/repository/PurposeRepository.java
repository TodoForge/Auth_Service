package com.auth.Auth.Service.repository;

import com.auth.Auth.Service.entity.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, UUID> {
    Optional<Purpose> findByName(String name);
}
