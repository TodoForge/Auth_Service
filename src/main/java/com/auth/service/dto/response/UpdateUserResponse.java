package com.auth.service.dto.response;

import com.auth.service.enums.Purpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserResponse {

    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String username;
    private LocalDate dateOfBirth;
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

    private String roleName;
    private boolean isActive;
    private boolean isValid;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
