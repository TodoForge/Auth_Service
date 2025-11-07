package com.auth.service.repository;

import com.auth.service.entity.PasswordResetToken;
import com.auth.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    
    // Find token by token string
    Optional<PasswordResetToken> findByToken(String token);
    
    // Find active token for user
    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);
    
    // Find token by user and token string
    Optional<PasswordResetToken> findByUserAndTokenAndUsedFalse(User user, String token);
    
    // Find valid token (not used and not expired)
    @Query("SELECT prt FROM PasswordResetToken prt " +
           "WHERE prt.token = :token AND prt.used = false AND prt.expiresAt > :now")
    Optional<PasswordResetToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    // Find all tokens for user
    List<PasswordResetToken> findByUser(User user);
    
    // Find expired tokens
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.expiresAt < :now")
    List<PasswordResetToken> findExpiredTokens(@Param("now") LocalDateTime now);
    
    // Find unused tokens
    List<PasswordResetToken> findByUsedFalse();
    
    // Find tokens by user and used status
    List<PasswordResetToken> findByUserAndUsed(User user, boolean used);
    
    // Delete expired tokens
    void deleteByExpiresAtBefore(LocalDateTime now);
    
    // Delete tokens by user
    void deleteByUser(User user);
    
    // Count active tokens for user
    Long countByUserAndUsedFalse(User user);
}
