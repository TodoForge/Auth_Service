package com.auth.service.controller;

import com.auth.service.dto.response.SignupResponse;
import com.auth.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<SignupResponse> getUserById(@PathVariable UUID id){
        return userService.getById(id);
    }
    
    @PostMapping("/complete-setup")
    public ResponseEntity<Map<String, String>> completeSetup(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        try {
            userService.markSetupCompleted(email);
            return ResponseEntity.ok(Map.of("message", "Setup completed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/complete-welcome")
    public ResponseEntity<Map<String, String>> completeWelcome(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        try {
            userService.markWelcomeCompleted(email);
            return ResponseEntity.ok(Map.of("message", "Welcome page marked as completed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
