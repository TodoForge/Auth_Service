package com.auth.Auth.Service.entity;

import com.auth.Auth.Service.enums.Purpose;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Enter your Name")
    @Size(max = 150, message = "Characters should not exceed more then 150")
    private String fullName;

    @NotBlank(message = "Enter your Email-Id")
    @Email
    @Size(max = 150, message = "Characters should not exceed more then 150")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Enter your Number")
    @Size(max = 10,min = 10, message = "Number should be exactly 10 digits")
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank(message = "Enter username")
    @Size(min = 8, max = 15, message = "Username should be 8-15 characters")
    @Column(unique = true)
    private String username;


    @NotBlank(message = "Enter Password")
    @Size(min = 8, message = "Password Should be more then 8 characters")
    private String password;

    @NotNull(message = "Enter DOB")
    private LocalDate dateOfBirth;


    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please select the Purpose of using")
    private Purpose purpose;

    private String department;
    private String workEmail;
    private String workLocation;

    private String companyName;
    private String companyWebsite;
    private String industry;
    private Integer numberOfEmployees;
    private String companyAddress;
    private String roleInCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = true)
    private Role role;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isValid = true;

    // New fields for enhanced functionality
    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerified = false;

    private LocalDateTime lastLogin;

    @Builder.Default
    @Column(nullable = false)
    private Integer loginAttempts = 0;

    private LocalDateTime lockedUntil;

    @Size(max = 500, message = "Profile picture URL must be less than 500 characters")
    private String profilePicture;

    @Builder.Default
    @Size(max = 50, message = "Timezone must be less than 50 characters")
    private String timezone = "UTC";

    @Builder.Default
    @Size(max = 10, message = "Language must be less than 10 characters")
    private String language = "en";

    // OneToMany relationships
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private java.util.List<com.auth.Auth.Service.entity.UserOrganization> userOrganizations = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private java.util.List<com.auth.Auth.Service.entity.UserSession> userSessions = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private java.util.List<com.auth.Auth.Service.entity.PasswordResetToken> passwordResetTokens = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private java.util.List<com.auth.Auth.Service.entity.EmailVerificationToken> emailVerificationTokens = new java.util.ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
