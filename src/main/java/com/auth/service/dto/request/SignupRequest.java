package com.auth.service.dto.request;

import com.auth.service.enums.Purpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank(message = "Enter your full name")
    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "Enter your email")
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Enter phone number")
    @Size(min = 10, max = 10, message = "Number should be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Enter username")
    @Size(min = 8, max = 15)
    private String username;

    @NotBlank(message = "Enter password")
    @Size(min = 8)
    private String password;

    @NotNull(message = "Enter date of birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Select purpose")
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
}
