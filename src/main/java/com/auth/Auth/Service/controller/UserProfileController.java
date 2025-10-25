package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.dto.request.UpdateUserProfileRequest;
import com.auth.Auth.Service.dto.response.UserProfileResponse;
import com.auth.Auth.Service.dto.response.UserSessionResponse;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.UserSession;
import com.auth.Auth.Service.service.UserService;
import com.auth.Auth.Service.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserSessionService userSessionService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .timezone(user.getTimezone())
                .language(user.getLanguage())
                .emailVerified(user.isEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .isActive(user.isActive())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @RequestBody UpdateUserProfileRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        
        // Update user fields if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
        if (request.getTimezone() != null) {
            user.setTimezone(request.getTimezone());
        }
        if (request.getLanguage() != null) {
            user.setLanguage(request.getLanguage());
        }
        
        User updatedUser = userService.updateUser(user);
        
        UserProfileResponse response = UserProfileResponse.builder()
                .id(updatedUser.getId())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .username(updatedUser.getUsername())
                .phoneNumber(updatedUser.getPhoneNumber())
                .profilePicture(updatedUser.getProfilePicture())
                .timezone(updatedUser.getTimezone())
                .language(updatedUser.getLanguage())
                .emailVerified(updatedUser.isEmailVerified())
                .lastLogin(updatedUser.getLastLogin())
                .createdAt(updatedUser.getCreatedAt())
                .isActive(updatedUser.isActive())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<UserSessionResponse>> getUserSessions(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<UserSession> sessions = userSessionService.getUserActiveSessions(user.getId());
        
        List<UserSessionResponse> responses = sessions.stream()
                .map(session -> UserSessionResponse.builder()
                        .id(session.getId())
                        .deviceInfo(session.getDeviceInfo())
                        .ipAddress(session.getIpAddress())
                        .createdAt(session.getCreatedAt())
                        .expiresAt(session.getExpiresAt())
                        .isActive(session.isActive())
                        .build())
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> invalidateSession(
            @PathVariable UUID sessionId,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        
        // Get session and verify it belongs to the user
        try {
            UserSession session = userSessionService.getSessionByToken(sessionId.toString());
            if (!session.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            userSessionService.invalidateSession(sessionId.toString());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> invalidateAllSessions(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userSessionService.invalidateAllUserSessions(user.getId());
        
        return ResponseEntity.ok().build();
    }
}
