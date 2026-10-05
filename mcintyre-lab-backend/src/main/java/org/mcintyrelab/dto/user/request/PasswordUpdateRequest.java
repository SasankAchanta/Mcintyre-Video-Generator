package org.mcintyrelab.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password cannot be blank")
        @Size(min = 8, max = 64, message = "New password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String newPassword
) {
    // Our compact constructor practice!
    public PasswordUpdateRequest {
        // Fast-fail if the raw input text strings match exactly
        if (currentPassword != null && currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password cannot be the same as your current password");
        }
    }
}
