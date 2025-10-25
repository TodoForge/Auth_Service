package com.auth.Auth.Service.service.impl;

import com.auth.Auth.Service.security.JwtUtil;
import com.auth.Auth.Service.dto.request.LoginRequest;
import com.auth.Auth.Service.dto.request.SignupRequest;
import com.auth.Auth.Service.dto.response.LoginResponse;
import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.entity.Role;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.mapper.UserMapper;
import com.auth.Auth.Service.repository.RoleRepository;
import com.auth.Auth.Service.repository.UserRepository;
import com.auth.Auth.Service.service.AuthService;
import com.auth.Auth.Service.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public SignupResponse registerUser(SignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email Already Exist");
        } else if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone Number Already Exist");
        } else if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username Already Exist");
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // ALL users become ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        user.setRole(adminRole);
        
        // Generate organizationId for each user
        user.setOrganizationId(UUID.randomUUID());
        user.setActive(true);
        user.setValid(true);
        
        User savedUser = userRepository.save(user);
        SignupResponse response = UserMapper.toResponse(savedUser);
        response.setMessage("Admin Registered Successfully");
        return response;
    }

    @Override
    public LoginResponse loginUser(LoginRequest request, HttpServletResponse response) {
        String usernameOrEmail = request.getUsernameOrEmail();
        String password = request.getPassword();
        
        // First check if user exists by username or email
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElse(null));
        
        if (user == null) {
            throw new RuntimeException("Invalid email or username");
        }
        
        // Check if password is correct
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        
        // Check if user is active
        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated. Contact admin.");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(15*60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24*60*60)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return LoginResponse.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .isActive(user.isActive())
                .message("Login Successful")
                .build();
    }


    @Override
    public Map<String, String> logout(HttpServletResponse response) {
        // Clear access token cookie
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        
        // Clear refresh token cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Logged out successfully");
        return result;
    }
    
    // Helper method to get current user from authentication
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username)));
    }
    
    // Admin method to add users to organization
    public SignupResponse addUserToOrganization(SignupRequest request, Authentication authentication) {
        // Get current admin's organizationId
        User admin = getCurrentUser(authentication);
        UUID adminOrganizationId = admin.getOrganizationId();
        
        // Check if user already exists
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email Already Exist");
        } else if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone Number Already Exist");
        } else if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username Already Exist");
        }

        // Create subuser with same organizationId as admin
        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOrganizationId(adminOrganizationId); // Set to admin's organizationId
        
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        user.setRole(userRole);
        
        user.setActive(true);
        user.setValid(true);
        
        User savedUser = userRepository.save(user);
        SignupResponse response = UserMapper.toResponse(savedUser);
        response.setMessage("User added to organization successfully");
        return response;
    }
}
