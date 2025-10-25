package com.auth.Auth.Service.service;

public interface PasswordResetService {
    
    // Request password reset
    void requestPasswordReset(String email);
    
    // Reset password with token
    void resetPassword(String token, String newPassword);
    
    // Check if token is valid
    boolean isTokenValid(String token);
    
    // Clean up expired tokens
    void cleanupExpiredTokens();
}
