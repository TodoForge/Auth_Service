package com.auth.service.service.impl;

import com.auth.service.entity.EmailVerificationToken;
import com.auth.service.entity.User;
import com.auth.service.repository.EmailVerificationTokenRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.service.EmailVerificationService;
import com.auth.service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${email.verification.token.expiration:86400}") // 24 hours default
    private int tokenExpirationSeconds;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendVerificationEmail(UUID userId) {
        try {
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

            // Send email with verification link
            emailService.sendVerificationEmail(user.getEmail(), token);
        } catch (Exception e) {
            System.err.println("Error in sendVerificationEmail: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendVerificationEmailByEmail(String email) {
        System.out.println("Looking for user with email: " + email);
        
        // Try case-insensitive lookup
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    System.out.println("Case-insensitive lookup failed, trying exact match");
                    return userRepository.findByEmail(email).orElse(null);
                });
        
        if (user == null) {
            System.err.println("User not found with email: " + email);
            System.err.println("Available users in database:");
            userRepository.findAll().forEach(u -> System.err.println("  - " + u.getEmail()));
            throw new RuntimeException("User not found with email: " + email);
        }
        
        System.out.println("Found user: " + user.getEmail() + " (ID: " + user.getId() + ")");

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

        // Send email with verification link
        emailService.sendVerificationEmail(user.getEmail(), token);
        
        System.out.println("Verification email sent to: " + email);
        System.out.println("Verification token: " + token);
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository
                .findValidToken(token, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        User user = verificationToken.getUser();

        // Mark email as verified and activate user
        user.setEmailVerified(true);
        user.setActive(true); // Activate user after email verification
        userRepository.save(user);

        // Mark token as verified
        verificationToken.setVerified(true);
        emailVerificationTokenRepository.save(verificationToken);
        
        // Send welcome email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
            // Don't fail verification if welcome email fails
        }
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
