package com.auth.Auth.Service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationSetupRequest {
    
    private List<String> integrations;
    private String purpose; // PERSONAL, WORK, etc.
    private String teamSize; // INDIVIDUAL, SMALL_TEAM, LARGE_TEAM
}
