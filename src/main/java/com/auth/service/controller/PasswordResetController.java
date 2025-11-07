package com.auth.service.controller;

import com.auth.service.dto.request.PasswordResetConfirmRequest;
import com.auth.service.dto.request.PasswordResetRequest;
import com.auth.service.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(Map.of(
                "message", "Password reset email sent successfully",
                "status", "success"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }

    @PostMapping("/reset-confirm")
    public ResponseEntity<Map<String, String>> confirmPasswordReset(@RequestBody PasswordResetConfirmRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully",
                "status", "success"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        boolean isValid = passwordResetService.isTokenValid(token);
        return ResponseEntity.ok(Map.of(
            "valid", isValid,
            "message", isValid ? "Token is valid" : "Token is invalid or expired"
        ));
    }
}
