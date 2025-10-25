package com.auth.Auth.Service.service;

import com.auth.Auth.Service.dto.response.UserSessionResponse;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.UserSession;

import java.util.List;
import java.util.UUID;

public interface UserSessionService {
    
    // Create new session
    UserSession createSession(User user, String deviceInfo, String ipAddress, String userAgent);
    
    // Invalidate specific session
    void invalidateSession(String sessionToken);
    
    // Invalidate all user sessions
    void invalidateAllUserSessions(UUID userId);
    
    // Get user's active sessions
    List<UserSession> getUserActiveSessions(UUID userId);
    
    // Check if session is valid
    boolean isSessionValid(String sessionToken);
    
    // Get session by token
    UserSession getSessionByToken(String sessionToken);
    
    // Update session activity
    void updateSessionActivity(String sessionToken);
    
    // Clean up expired sessions
    void cleanupExpiredSessions();
    
    // Get user sessions count
    Long getUserSessionsCount(UUID userId);
    
    // Check if user has too many sessions
    boolean hasTooManySessions(UUID userId, int maxSessions);
}
