package com.auth.Auth.Service.service;

import java.util.UUID;

public interface EmailVerificationService {
    
    // Send verification email
    void sendVerificationEmail(UUID userId);
    
    // Send verification email by email address
    void sendVerificationEmailByEmail(String email);
    
    // Verify email with token
    void verifyEmail(String token);
    
    // Check if email is verified
    boolean isEmailVerified(UUID userId);
    
    // Clean up expired tokens
    void cleanupExpiredTokens();
}
