package com.auth.service.service;

import com.auth.service.dto.response.IntegrationResponse;
import com.auth.service.entity.User;

import java.util.List;

public interface IntegrationService {
    List<IntegrationResponse> getAllIntegrations();
    void selectIntegrations(User user, List<String> integrationNames);
}
