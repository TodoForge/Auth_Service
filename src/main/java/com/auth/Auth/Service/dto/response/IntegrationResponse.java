package com.auth.Auth.Service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationResponse {
    private UUID id;
    private String name;
    private String description;
    private String iconUrl;
}
