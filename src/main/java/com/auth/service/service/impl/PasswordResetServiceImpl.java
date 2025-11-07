package com.auth.service.service.impl;

import com.auth.service.entity.PasswordResetToken;
import com.auth.service.entity.User;
import com.auth.service.repository.PasswordResetTokenRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${password.reset.token.expiration:3600}") // 1 hour default
    private int tokenExpirationSeconds;

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Invalidate existing tokens for this user
        passwordResetTokenRepository.findByUserAndUsedFalse(user)
                .ifPresent(token -> {
                    token.setUsed(true);
                    passwordResetTokenRepository.save(token);
                });

        // Generate new token
        String token = generateResetToken();

        // Create password reset token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(tokenExpirationSeconds))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // TODO: Send email with reset link
        // emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findValidToken(token, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        User user = resetToken.getUser();

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenValid(String token) {
        return passwordResetTokenRepository.findValidToken(token, LocalDateTime.now()).isPresent();
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
