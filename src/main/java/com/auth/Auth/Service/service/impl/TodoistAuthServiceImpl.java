package com.auth.Auth.Service.service.impl;

import com.auth.Auth.Service.dto.request.TodoistSignupRequest;
import com.auth.Auth.Service.dto.request.IntegrationSetupRequest;
import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.dto.response.LoginResponse;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.Role;
import com.auth.Auth.Service.entity.Organization;
import com.auth.Auth.Service.repository.UserRepository;
import com.auth.Auth.Service.repository.RoleRepository;
import com.auth.Auth.Service.repository.OrganizationRepository;
import com.auth.Auth.Service.service.TodoistAuthService;
import com.auth.Auth.Service.service.EmailVerificationService;
import com.auth.Auth.Service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoistAuthServiceImpl implements TodoistAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final JwtUtil jwtUtil;

    private String generateUsername(String email) {
        String baseUsername = email.split("@")[0];
        // Ensure username is between 8-15 characters
        if (baseUsername.length() > 12) {
            baseUsername = baseUsername.substring(0, 12);
        }
        // Add a short suffix to make it unique and meet minimum length
        String suffix = String.valueOf(System.currentTimeMillis() % 10000); // Last 4 digits
        String username = baseUsername + suffix;
        
        // Ensure it's within 8-15 character limit
        if (username.length() > 15) {
            username = username.substring(0, 15);
        }
        if (username.length() < 8) {
            username = username + "0000".substring(0, 8 - username.length());
        }
        
        return username;
    }

    @Override
    public SignupResponse signup(TodoistSignupRequest request) {
        try {
            // Check if user already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            // Create user with minimal fields (like Todoist)
            User user = User.builder()
                    .fullName(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .username(generateUsername(request.getEmail()))
                    .phoneNumber("0000000000") // Default
                    .dateOfBirth(LocalDate.of(1990, 1, 1)) // Default
                    .purpose(com.auth.Auth.Service.enums.Purpose.PERSONAL) // Default
                    .department("Default") // Default
                    .workEmail(request.getEmail()) // Same as email
                    .workLocation("Default") // Default
                    .companyName("Default Company") // Default
                    .companyWebsite("https://default.com") // Default
                    .industry("Technology") // Default
                    .numberOfEmployees(1) // Default
                    .companyAddress("Default Address") // Default
                    .roleInCompany("Employee") // Default
                    .isActive(false) // Inactive until email verification
                    .isValid(true)
                    .emailVerified(false)
                    .build();

            // Set role
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ADMIN");
                        newRole.setDescription("Administrator role");
                        return roleRepository.save(newRole);
                    });
            user.setRole(adminRole);

            // Save user
            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());
            System.out.println("User email: " + savedUser.getEmail());
            System.out.println("User username: " + savedUser.getUsername());
            System.out.println("User is active: " + savedUser.isActive());

            // Create or get default organization
            Organization defaultOrg = organizationRepository.findAll().stream()
                    .findFirst()
                    .orElseGet(() -> {
                        Organization newOrg = Organization.builder()
                                .name("Default Organization")
                                .description("Default organization for new users")
                                .isActive(true)
                                .createdBy(savedUser)
                                .build();
                        return organizationRepository.save(newOrg);
                    });

            // Link user to organization
            savedUser.setOrganizationId(defaultOrg.getId());
            userRepository.save(savedUser);

            // Return response first
            SignupResponse response = SignupResponse.builder()
                    .id(savedUser.getId())
                    .fullName(savedUser.getFullName())
                    .email(savedUser.getEmail())
                    .username(savedUser.getUsername())
                    .phoneNumber(savedUser.getPhoneNumber())
                    .purpose(savedUser.getPurpose())
                    .isActive(savedUser.isActive())
                    .message("Registration successful! Please check your email to verify your account.")
                    .build();

            // Send email verification AFTER transaction commit
            // This will be handled by a separate transaction
            try {
                System.out.println("Attempting to send verification email to: " + savedUser.getEmail());
                emailVerificationService.sendVerificationEmailByEmail(savedUser.getEmail());
                System.out.println("Verification email sent successfully to: " + savedUser.getEmail());
            } catch (Exception e) {
                System.err.println("Failed to send verification email: " + e.getMessage());
                e.printStackTrace();
                // Don't fail registration if email sending fails
                System.out.println("Registration completed successfully, but email verification failed");
            }

            return response;

        } catch (Exception e) {
            System.err.println("Signup failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Signup failed: " + e.getMessage(), e);
        }
    }

    @Override
    public SignupResponse setupIntegrations(String email, IntegrationSetupRequest request) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isEmailVerified()) {
                throw new RuntimeException("Email not verified yet");
            }

            // Update user with integration preferences
            // This is where you'd store the integration preferences
            // For now, we'll just activate the user
            user.setActive(true);
            userRepository.save(user);

            return SignupResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .isActive(user.isActive())
                    .message("Account setup complete! Welcome to Todoist.")
                    .build();

        } catch (Exception e) {
            System.err.println("Integration setup failed: " + e.getMessage());
            throw new RuntimeException("Integration setup failed: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginResponse login(String email, String password) {
        try {
            System.out.println("Attempting login for email: " + email);
            
            // Try case-insensitive lookup first
            User user = userRepository.findByEmailIgnoreCase(email)
                    .orElseGet(() -> {
                        System.out.println("Case-insensitive lookup failed, trying exact match");
                        return userRepository.findByEmail(email).orElse(null);
                    });
            
            if (user == null) {
                System.err.println("User not found with email: " + email);
                System.err.println("Available users in database:");
                userRepository.findAll().forEach(u -> System.err.println("  - " + u.getEmail()));
                throw new RuntimeException("User not found");
            }
            
            System.out.println("Found user: " + user.getEmail() + " (ID: " + user.getId() + ")");
            System.out.println("User is active: " + user.isActive());
            System.out.println("User email verified: " + user.isEmailVerified());

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            if (!user.isActive()) {
                throw new RuntimeException("Account not activated. Please verify your email first.");
            }

            // Generate both access and refresh tokens
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            System.out.println("Generated access token: " + accessToken);
            System.out.println("Generated refresh token: " + refreshToken);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .roleName(user.getRole().getName())
                    .isActive(user.isActive())
                    .message("Login successful")
                    .build();

        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}
