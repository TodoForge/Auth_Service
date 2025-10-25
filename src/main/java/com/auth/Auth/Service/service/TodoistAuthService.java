package com.auth.Auth.Service.service;

import com.auth.Auth.Service.dto.request.TodoistSignupRequest;
import com.auth.Auth.Service.dto.request.IntegrationSetupRequest;
import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.dto.response.LoginResponse;

public interface TodoistAuthService {
    
    // Step 1: Basic signup (name, email, password only)
    SignupResponse signup(TodoistSignupRequest request);
    
    // Step 2: Setup integrations after email verification
    SignupResponse setupIntegrations(String email, IntegrationSetupRequest request);
    
    // Login
    LoginResponse login(String email, String password);
}
