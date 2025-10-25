package com.auth.Auth.Service.repository;

import com.auth.Auth.Service.entity.EmailVerificationToken;
import com.auth.Auth.Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {
    
    // Find token by token string
    Optional<EmailVerificationToken> findByToken(String token);
    
    // Find active token for user
    Optional<EmailVerificationToken> findByUserAndVerifiedFalse(User user);
    
    // Find token by user and token string
    Optional<EmailVerificationToken> findByUserAndTokenAndVerifiedFalse(User user, String token);
    
    // Find valid token (not verified and not expired)
    @Query("SELECT evt FROM EmailVerificationToken evt " +
           "WHERE evt.token = :token AND evt.verified = false AND evt.expiresAt > :now")
    Optional<EmailVerificationToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    // Find all tokens for user
    List<EmailVerificationToken> findByUser(User user);
    
    // Find expired tokens
    @Query("SELECT evt FROM EmailVerificationToken evt WHERE evt.expiresAt < :now")
    List<EmailVerificationToken> findExpiredTokens(@Param("now") LocalDateTime now);
    
    // Find unverified tokens
    List<EmailVerificationToken> findByVerifiedFalse();
    
    // Find tokens by user and verified status
    List<EmailVerificationToken> findByUserAndVerified(User user, boolean verified);
    
    // Delete expired tokens
    void deleteByExpiresAtBefore(LocalDateTime now);
    
    // Delete tokens by user
    void deleteByUser(User user);
    
    // Count active tokens for user
    Long countByUserAndVerifiedFalse(User user);
    
    // Check if user has verified email
    @Query("SELECT COUNT(evt) > 0 FROM EmailVerificationToken evt " +
           "WHERE evt.user = :user AND evt.verified = true")
    boolean hasVerifiedEmail(@Param("user") User user);
}
