package com.auth.Auth.Service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrganizationRequest {

    @NotBlank(message = "Organization name is required")
    @Size(max = 255, message = "Organization name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Size(max = 100, message = "Industry must be less than 100 characters")
    private String industry;

    @Size(max = 50, message = "Company size must be less than 50 characters")
    private String companySize;

    @Size(max = 255, message = "Website must be less than 255 characters")
    private String website;

    @Size(max = 1000, message = "Address must be less than 1000 characters")
    private String address;
}
