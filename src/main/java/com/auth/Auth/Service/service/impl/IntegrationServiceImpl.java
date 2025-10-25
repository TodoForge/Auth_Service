package com.auth.Auth.Service.service.impl;

import com.auth.Auth.Service.dto.response.IntegrationResponse;
import com.auth.Auth.Service.entity.Integration;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.UserIntegration;
import com.auth.Auth.Service.repository.IntegrationRepository;
import com.auth.Auth.Service.repository.UserIntegrationRepository;
import com.auth.Auth.Service.repository.UserRepository;
import com.auth.Auth.Service.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntegrationServiceImpl implements IntegrationService {
    
    private final IntegrationRepository integrationRepository;
    private final UserIntegrationRepository userIntegrationRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<IntegrationResponse> getAllIntegrations() {
        return integrationRepository.findAll().stream()
                .map(integration -> IntegrationResponse.builder()
                        .id(integration.getId())
                        .name(integration.getName())
                        .description(integration.getDescription())
                        .iconUrl(integration.getIconUrl())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void selectIntegrations(User user, List<String> integrationNames) {
        for (String integrationName : integrationNames) {
            Integration integration = integrationRepository.findByName(integrationName)
                    .orElseThrow(() -> new RuntimeException("Integration not found: " + integrationName));
            
            // Check if user already has this integration
            if (!userIntegrationRepository.existsByUserAndIntegration(user, integration)) {
                UserIntegration userIntegration = UserIntegration.builder()
                        .user(user)
                        .integration(integration)
                        .isConnected(false) // Initially not connected
                        .build();
                userIntegrationRepository.save(userIntegration);
            }
        }
        
        // Mark setup as completed
        user.setSetupCompleted(true);
        userRepository.save(user);
    }
}
