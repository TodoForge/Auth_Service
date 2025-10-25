package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.dto.request.LoginRequest;
import com.auth.Auth.Service.dto.request.SignupRequest;
import com.auth.Auth.Service.dto.response.LoginResponse;
import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.service.AuthService;
import com.auth.Auth.Service.service.EmailVerificationService;
import com.auth.Auth.Service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<SignupResponse> register(@RequestBody SignupRequest request){
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response){
        LoginResponse loginResponse = authService.loginUser(request, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response){
        Map<String, String> result = authService.logout(response);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test(){
        return ResponseEntity.ok(Map.of("message", "Auth Service is working!"));
    }

    @GetMapping("/test-user/{email}")
    public ResponseEntity<Map<String, Object>> testUser(@PathVariable String email) {
        try {
            // Try to find user by email
            var user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "found", true,
                    "email", user.get().getEmail(),
                    "id", user.get().getId().toString(),
                    "active", user.get().isActive(),
                    "emailVerified", user.get().isEmailVerified()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "found", false,
                    "message", "User not found with email: " + email
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/test-all-users")
    public ResponseEntity<Map<String, Object>> testAllUsers() {
        try {
            var users = userRepository.findAll();
            var userList = users.stream().map(user -> Map.of(
                "id", user.getId().toString(),
                "email", user.getEmail(),
                "username", user.getUsername(),
                "active", user.isActive(),
                "emailVerified", user.isEmailVerified()
            )).toList();
            
            return ResponseEntity.ok(Map.of(
                "totalUsers", users.size(),
                "users", userList
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/admin/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SignupResponse> addUser(@RequestBody SignupRequest request, Authentication authentication){
        SignupResponse response = authService.addUserToOrganization(request, authentication);
        return ResponseEntity.ok(response);
    }

    // Public email verification endpoints
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, String>> sendVerificationEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email is required",
                    "status", "error"
                ));
            }
            
            // Find user by email and send verification
            // This is a simplified approach - in production, you might want to add rate limiting
            emailVerificationService.sendVerificationEmailByEmail(email);
            
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

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Token is required",
                    "status", "error"
                ));
            }
            
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

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerificationEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email is required",
                    "status", "error"
                ));
            }
            
            emailVerificationService.sendVerificationEmailByEmail(email);
            
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

    // Password reset endpoints (placeholder implementations)
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email is required",
                    "status", "error"
                ));
            }
            
            // TODO: Implement password reset logic
            System.out.println("Password reset requested for: " + email);
            
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

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Token is required",
                    "status", "error"
                ));
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "New password is required",
                    "status", "error"
                ));
            }
            
            // TODO: Implement password reset logic
            System.out.println("Password reset with token: " + token);
            
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

}
