package com.auth.service.repository;

import com.auth.service.entity.User;
import com.auth.service.entity.UserIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserIntegrationRepository extends JpaRepository<UserIntegration, UUID> {
    List<UserIntegration> findByUser(User user);
    boolean existsByUserAndIntegration(User user, com.auth.service.entity.Integration integration);
}
