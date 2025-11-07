package com.auth.service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequest {

    @Size(max = 150, message = "Full name must be less than 150 characters")
    private String fullName;

    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @Size(max = 500, message = "Profile picture URL must be less than 500 characters")
    private String profilePicture;

    @Size(max = 50, message = "Timezone must be less than 50 characters")
    private String timezone;

    @Size(max = 10, message = "Language must be less than 10 characters")
    private String language;
}
