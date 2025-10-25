package com.auth.Auth.Service.service;

import com.auth.Auth.Service.dto.response.IntegrationResponse;
import com.auth.Auth.Service.entity.User;

import java.util.List;

public interface IntegrationService {
    List<IntegrationResponse> getAllIntegrations();
    void selectIntegrations(User user, List<String> integrationNames);
}
