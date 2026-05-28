package org.mcintyrelab.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UsernameUpdateRequest (
        @NotNull(message = "Target user ID is required")
        UUID targetUserId,

        @NotBlank(message = "New username cannot be blank")
        @Pattern(
                regexp = "^[a-z]{3}\\d{6}$",
                message = "Lab code must be exactly 3 lowercase letters followed by 6 numbers (e.g., abc123456)"
        )
        String newUsername,

        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reason
){
    public UsernameUpdateRequest {
        // Automatically clean up casing and trailing/leading spaces
        if (newUsername != null) {
            newUsername = newUsername.trim().toLowerCase();
        }
    }
}
