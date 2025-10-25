package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.dto.request.LoginRequest;
import com.auth.Auth.Service.dto.request.SignupRequest;
import com.auth.Auth.Service.dto.response.LoginResponse;
import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.service.AuthService;
import com.auth.Auth.Service.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
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

    @PostMapping("/register")
    public ResponseEntity<SignupResponse> register(@RequestBody SignupRequest request){
        try {
            return ResponseEntity.ok(authService.registerUser(request));
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(SignupResponse.builder()
                .message("Registration failed: " + e.getMessage())
                .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response){
        LoginResponse loginResponse = authService.loginUser(request, response);
        
        // Get tokens from the service response (before they're removed from response body)
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();
        
        // Set access token cookie
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(15*60) // 15 minutes
                .sameSite("Lax")
                .build();
        
        // Set refresh token cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(24*60*60) // 24 hours
                .sameSite("Lax")
                .build();
        
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        
        // Return response without token
        LoginResponse responseBody = LoginResponse.builder()
                .username(loginResponse.getUsername())
                .fullName(loginResponse.getFullName())
                .email(loginResponse.getEmail())
                .roleName(loginResponse.getRoleName())
                .isActive(loginResponse.isActive())
                .message("Login successful")
                .build();
        
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response){
        Map<String, String> result = authService.logout(response);
        return ResponseEntity.ok(result);
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
