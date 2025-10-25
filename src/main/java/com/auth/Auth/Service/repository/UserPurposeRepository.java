package com.auth.Auth.Service.repository;

import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.UserPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserPurposeRepository extends JpaRepository<UserPurpose, UUID> {
    List<UserPurpose> findByUser(User user);
    boolean existsByUserAndPurpose(User user, com.auth.Auth.Service.entity.Purpose purpose);
}
