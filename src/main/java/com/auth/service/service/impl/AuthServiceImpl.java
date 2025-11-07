package com.auth.service.service.impl;

import com.auth.service.security.JwtUtil;
import com.auth.service.dto.request.LoginRequest;
import com.auth.service.dto.request.SignupRequest;
import com.auth.service.dto.response.LoginResponse;
import com.auth.service.dto.response.SignupResponse;
import com.auth.service.entity.Role;
import com.auth.service.entity.User;
import com.auth.service.mapper.UserMapper;
import com.auth.service.repository.RoleRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.repository.OrganizationRepository;
import com.auth.service.service.AuthService;
import com.auth.service.service.UserService;
import com.auth.service.service.EmailVerificationService;
import com.auth.service.entity.Organization;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

   @Override
public SignupResponse registerUser(SignupRequest request) {
    try {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email Already Exist");
        } else if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone Number Already Exist");
        } else if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username Already Exist");
        }

        System.out.println("Creating user with email: " + request.getEmail());
        User user = UserMapper.toEntity(request);
        System.out.println("User entity created: " + (user != null ? "SUCCESS" : "FAILED"));
        
        if (user == null) {
            throw new RuntimeException("Failed to create user entity");
        }
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        System.out.println("Password encoded successfully");

    // ALL users become ADMIN - create role if it doesn't exist
    Role adminRole = roleRepository.findByName("ADMIN")
            .orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("ADMIN");
                newRole.setDescription("Administrator role");
                return roleRepository.save(newRole);
            });
    user.setRole(adminRole);
    user.setActive(false); // User should be inactive until email verification
    user.setValid(true);
    user.setEmailVerified(false); // Email not verified yet

    // ✅ 1. Save the user first
    System.out.println("About to save user with email: " + user.getEmail());
    User savedUser = userRepository.save(user);
    System.out.println("User saved successfully with ID: " + savedUser.getId() + " and email: " + savedUser.getEmail());

    // ✅ 2. Create default organization with savedUser as creator
    Organization defaultOrg = organizationRepository.findAll().stream()
            .findFirst()
            .orElseGet(() -> {
                Organization newOrg = Organization.builder()
                        .name("Default Organization")
                        .description("Default organization for new users")
                        .isActive(true)
                        .createdBy(savedUser) // ✅ now persistent
                        .build();
                return organizationRepository.save(newOrg);
            });

    // ✅ 3. Link user to organization
    savedUser.setOrganizationId(defaultOrg.getId());
    userRepository.save(savedUser);

    SignupResponse response = UserMapper.toResponse(savedUser);
    response.setMessage("Registration successful! Please check your email to verify your account.");
    
    // ✅ 4. Send email verification (using user ID to avoid lookup issues)
    try {
        System.out.println("Attempting to send verification email to: " + savedUser.getEmail());
        emailVerificationService.sendVerificationEmail(savedUser.getId());
        System.out.println("Verification email sent successfully to: " + savedUser.getEmail());
    } catch (Exception e) {
        System.err.println("Failed to send verification email: " + e.getMessage());
        e.printStackTrace();
        // Don't fail registration if email sending fails - just log the error
        System.out.println("Registration completed successfully, but email verification failed");
    }
    
    return response;
    } catch (Exception e) {
        System.err.println("Registration failed with error: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Registration failed: " + e.getMessage(), e);
    }
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
        
        // Check if user is active (email verified)
        if (!user.isActive()) {
            if (!user.isEmailVerified()) {
                throw new RuntimeException("Please verify your email address before logging in. Check your inbox for the verification link.");
            } else {
                throw new RuntimeException("Account is deactivated. Contact admin.");
            }
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .isActive(user.isActive())
                .setupCompleted(user.isSetupCompleted())
                .welcomeCompleted(user.isWelcomeCompleted())
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
        
        // Temporarily commented out to isolate transaction issue
        // Role userRole = roleRepository.findByName("USER")
        //         .orElseThrow(() -> new RuntimeException("USER role not found"));
        // user.setRole(userRole);
        
        user.setActive(true);
        user.setValid(true);
        
        User savedUser = userRepository.save(user);
        SignupResponse response = UserMapper.toResponse(savedUser);
        response.setMessage("User added to organization successfully");
        return response;
    }
}
