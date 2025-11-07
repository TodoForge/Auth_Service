package com.auth.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Enter username or email")
    private String usernameOrEmail;

    @NotBlank(message = "Enter password")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
