package com.auth.service.controller;

import com.auth.service.dto.request.PurposeSelectionRequest;
import com.auth.service.dto.request.IntegrationSelectionRequest;
import com.auth.service.dto.response.PurposeResponse;
import com.auth.service.dto.response.IntegrationResponse;
import com.auth.service.entity.User;
import com.auth.service.repository.UserRepository;
import com.auth.service.service.PurposeService;
import com.auth.service.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", maxAge = 3600)
@RequiredArgsConstructor
public class RegistrationFlowController {
    
    private final PurposeService purposeService;
    private final IntegrationService integrationService;
    private final UserRepository userRepository;
    
    // Get all available purposes
    @GetMapping("/purposes")
    public ResponseEntity<List<PurposeResponse>> getPurposes() {
        List<PurposeResponse> purposes = purposeService.getAllPurposes();
        return ResponseEntity.ok(purposes);
    }
    
    // Get all available integrations
    @GetMapping("/integrations")
    public ResponseEntity<List<IntegrationResponse>> getIntegrations() {
        List<IntegrationResponse> integrations = integrationService.getAllIntegrations();
        return ResponseEntity.ok(integrations);
    }
    
    // Select purpose for user
    @PostMapping("/select-purpose")
    public ResponseEntity<Map<String, String>> selectPurpose(
            @RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String purpose = request.get("purpose");
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            
            purposeService.selectPurpose(user, purpose);
            
            return ResponseEntity.ok(Map.of("message", "Purpose selected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Select integrations for user
    @PostMapping("/select-integrations")
    public ResponseEntity<Map<String, String>> selectIntegrations(
            @RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            @SuppressWarnings("unchecked")
            List<String> integrations = (List<String>) request.get("integrations");
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            
            integrationService.selectIntegrations(user, integrations);
            
            return ResponseEntity.ok(Map.of("message", "Integrations selected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
