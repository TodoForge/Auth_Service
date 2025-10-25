package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/email")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, String>> sendVerificationEmail(Authentication authentication) {
        try {
            // Get user from authentication principal
            com.auth.Auth.Service.entity.User user = (com.auth.Auth.Service.entity.User) authentication.getPrincipal();
            emailVerificationService.sendVerificationEmail(user.getId());
            return ResponseEntity.ok(Map.of(
                "message", "Verification email sent successfully",
                "status", "success"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        try {
            emailVerificationService.verifyEmail(token);
            return ResponseEntity.ok(Map.of(
                "message", "Email verified successfully",
                "status", "success"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getVerificationStatus(Authentication authentication) {
        try {
            // Get user from authentication principal
            com.auth.Auth.Service.entity.User user = (com.auth.Auth.Service.entity.User) authentication.getPrincipal();
            boolean isVerified = emailVerificationService.isEmailVerified(user.getId());
            return ResponseEntity.ok(Map.of(
                "verified", isVerified,
                "message", isVerified ? "Email is verified" : "Email is not verified"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }
}
