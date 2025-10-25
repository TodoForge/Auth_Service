package com.auth.Auth.Service.dto.response;

import com.auth.Auth.Service.enums.Purpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponse {

    private UUID id;
    private String fullName;
    private String email;
    private String username;
    private String phoneNumber;
    private Purpose purpose;
    private boolean isActive;
    private String message;

}
