package com.auth.service.service.impl;

import com.auth.service.entity.User;
import com.auth.service.entity.UserSession;
import com.auth.service.repository.UserRepository;
import com.auth.service.repository.UserSessionRepository;
import com.auth.service.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    @Value("${session.timeout:3600}") // 1 hour default
    private int sessionTimeoutSeconds;

    @Value("${session.max-sessions:5}") // 5 sessions max default
    private int maxSessions;

    @Override
    public UserSession createSession(User user, String deviceInfo, String ipAddress, String userAgent) {
        // Check if user has too many sessions
        if (hasTooManySessions(user.getId(), maxSessions)) {
            // Remove oldest session
            List<UserSession> userSessions = userSessionRepository.findByUserAndIsActiveTrue(user);
            if (!userSessions.isEmpty()) {
                UserSession oldestSession = userSessions.get(0);
                oldestSession.setActive(false);
                userSessionRepository.save(oldestSession);
            }
        }

        // Generate session token
        String sessionToken = generateSessionToken();

        // Create session
        UserSession session = UserSession.builder()
                .user(user)
                .sessionToken(sessionToken)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(LocalDateTime.now().plusSeconds(sessionTimeoutSeconds))
                .isActive(true)
                .build();

        return userSessionRepository.save(session);
    }

    @Override
    public void invalidateSession(String sessionToken) {
        UserSession session = userSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setActive(false);
        userSessionRepository.save(session);
    }

    @Override
    public void invalidateAllUserSessions(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserSession> userSessions = userSessionRepository.findByUserAndIsActiveTrue(user);
        userSessions.forEach(session -> {
            session.setActive(false);
            userSessionRepository.save(session);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getUserActiveSessions(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userSessionRepository.findByUserAndIsActiveTrue(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionValid(String sessionToken) {
        return userSessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .map(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSession getSessionByToken(String sessionToken) {
        return userSessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .orElseThrow(() -> new RuntimeException("Invalid or expired session"));
    }

    @Override
    public void updateSessionActivity(String sessionToken) {
        UserSession session = getSessionByToken(sessionToken);
        session.setExpiresAt(LocalDateTime.now().plusSeconds(sessionTimeoutSeconds));
        userSessionRepository.save(session);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<UserSession> expiredSessions = userSessionRepository.findExpiredSessions(now);
        
        expiredSessions.forEach(session -> {
            session.setActive(false);
            userSessionRepository.save(session);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserSessionsCount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userSessionRepository.countByUserAndIsActiveTrue(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasTooManySessions(UUID userId, int maxSessions) {
        return getUserSessionsCount(userId) >= maxSessions;
    }

    private String generateSessionToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }
}
