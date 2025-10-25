package com.auth.Auth.Service.service.impl;

import com.auth.Auth.Service.entity.EmailVerificationToken;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.repository.EmailVerificationTokenRepository;
import com.auth.Auth.Service.repository.UserRepository;
import com.auth.Auth.Service.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;

    @Value("${email.verification.token.expiration:86400}") // 24 hours default
    private int tokenExpirationSeconds;

    @Override
    public void sendVerificationEmail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Invalidate existing tokens for this user
        emailVerificationTokenRepository.findByUserAndVerifiedFalse(user)
                .ifPresent(token -> {
                    token.setVerified(true);
                    emailVerificationTokenRepository.save(token);
                });

        // Generate new token
        String token = generateVerificationToken();

        // Create email verification token
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(tokenExpirationSeconds))
                .verified(false)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        // TODO: Send email with verification link
        // emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository
                .findValidToken(token, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        User user = verificationToken.getUser();

        // Mark email as verified
        user.setEmailVerified(true);
        userRepository.save(user);

        // Mark token as verified
        verificationToken.setVerified(true);
        emailVerificationTokenRepository.save(verificationToken);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailVerified(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.isEmailVerified();
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        emailVerificationTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
