package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.dto.request.TodoistSignupRequest;
import com.auth.Auth.Service.dto.request.IntegrationSetupRequest;
import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.dto.response.LoginResponse;
import com.auth.Auth.Service.service.TodoistAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/todoist")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", maxAge = 3600)
@RequiredArgsConstructor
public class TodoistController {

    private final TodoistAuthService todoistAuthService;

    // Step 1: Basic signup (name, email, password only)
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody TodoistSignupRequest request) {
        try {
            SignupResponse response = todoistAuthService.signup(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SignupResponse.builder()
                    .message("Signup failed: " + e.getMessage())
                    .build());
        }
    }

    // Step 2: Setup integrations after email verification
    @PostMapping("/setup-integrations")
    public ResponseEntity<SignupResponse> setupIntegrations(
            @RequestParam String email,
            @RequestBody IntegrationSetupRequest request) {
        try {
            SignupResponse response = todoistAuthService.setupIntegrations(email, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SignupResponse.builder()
                    .message("Integration setup failed: " + e.getMessage())
                    .build());
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = todoistAuthService.login(request.getEmail(), request.getPassword());
            
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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message("Login failed: " + e.getMessage())
                    .build());
        }
    }

    // Simple login request
    public static class LoginRequest {
        private String email;
        private String usernameOrEmail;
        private String password;
        
        public String getEmail() { 
            // Return email if set, otherwise return usernameOrEmail
            return email != null ? email : usernameOrEmail; 
        }
        public void setEmail(String email) { this.email = email; }
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
