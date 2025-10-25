package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/email-test")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping("/send-test")
    public ResponseEntity<Map<String, String>> sendTestEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email is required",
                    "status", "error"
                ));
            }
            
            // Send test verification email
            emailService.sendVerificationEmail(email, "test-token-123");
            
            return ResponseEntity.ok(Map.of(
                "message", "Test email sent successfully to " + email,
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Failed to send test email: " + e.getMessage(),
                "status", "error"
            ));
        }
    }
}
