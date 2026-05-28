package org.mcintyrelab.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        String firstname,

        @NotBlank(message = "Last name is required")
        String lastname,

        @NotBlank(message = "Email is required")
        // Enforces: 3 lowercase letters + 6 digits + @8chars.edu (Exactly 22 characters total)
        @Pattern(
                regexp = "^([a-z]{3}[0-9]{6}|[a-z]+\\.[a-z]+)@utdallas\\.edu$",
                message = "Email must match sequence (e.g., abc123456@utdallas.edu)"
        )
        String email,

        @NotBlank(message = "Username is required")
        // Enforces: Exactly 3 lowercase letters followed by exactly 6 digits
        @Pattern(
                regexp = "^[a-z]{3}\\d{6}$",
                message = "Username must be exactly 3 lowercase letters followed by 6 numbers"
        )
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        // Enforces: 1 uppercase, 1 lowercase, 1 digit, and 1 special character
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password
) {
    // Our compact constructor to clean up casing and spacing before validation checks them!
    public RegisterRequest {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (username != null) {
            username = username.trim().toLowerCase();
        }
    }
}