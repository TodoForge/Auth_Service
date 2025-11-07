package com.auth.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurposeSelectionRequest {
    @NotBlank(message = "Purpose is required")
    private String purpose;
    
    private String companyName;
    private String teamSize;
    private String industry;
}
