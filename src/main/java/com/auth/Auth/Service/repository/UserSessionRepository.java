package com.auth.Auth.Service.repository;

import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    
    // Find all sessions for a user
    List<UserSession> findByUser(User user);
    
    // Find session by token
    Optional<UserSession> findBySessionToken(String sessionToken);
    
    // Find active sessions for a user
    List<UserSession> findByUserAndIsActiveTrue(User user);
    
    // Find active session by token
    Optional<UserSession> findBySessionTokenAndIsActiveTrue(String sessionToken);
    
    // Find expired sessions
    @Query("SELECT us FROM UserSession us WHERE us.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);
    
    // Find sessions by device info
    List<UserSession> findByUserAndDeviceInfoAndIsActiveTrue(User user, String deviceInfo);
    
    // Find sessions by IP address
    List<UserSession> findByUserAndIpAddressAndIsActiveTrue(User user, String ipAddress);
    
    // Count active sessions for user
    Long countByUserAndIsActiveTrue(User user);
    
    // Find sessions created after specific time
    @Query("SELECT us FROM UserSession us WHERE us.user = :user AND us.createdAt > :after")
    List<UserSession> findByUserAndCreatedAtAfter(@Param("user") User user, @Param("after") LocalDateTime after);
    
    // Delete expired sessions
    void deleteByExpiresAtBeforeAndIsActiveFalse(LocalDateTime now);
    
    // Find sessions by user and active status
    List<UserSession> findByUserAndIsActive(User user, boolean isActive);
}
