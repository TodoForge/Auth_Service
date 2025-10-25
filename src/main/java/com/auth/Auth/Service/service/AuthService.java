package com.auth.Auth.Service.service;

import com.auth.Auth.Service.dto.request.LoginRequest;
import com.auth.Auth.Service.dto.request.SignupRequest;
import com.auth.Auth.Service.dto.response.LoginResponse;
import com.auth.Auth.Service.dto.response.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface AuthService {

    SignupResponse registerUser(SignupRequest request);

    LoginResponse loginUser(LoginRequest request, HttpServletResponse response);
    
    Map<String, String> logout(HttpServletResponse response);
    
    SignupResponse addUserToOrganization(SignupRequest request, Authentication authentication);
}
