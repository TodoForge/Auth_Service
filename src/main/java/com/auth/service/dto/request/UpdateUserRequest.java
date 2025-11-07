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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "Enter your Name")
    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "Enter your Phone Number")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotNull(message = "Enter DOB")
    private LocalDate dateOfBirth;

    @NotNull(message = "Please select the Purpose")
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
