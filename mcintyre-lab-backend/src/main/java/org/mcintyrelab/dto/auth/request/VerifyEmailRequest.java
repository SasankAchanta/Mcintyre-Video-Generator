package org.mcintyrelab.dto.auth.request;

import jakarta.validation.constraints.*;

public record VerifyEmailRequest(
        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^([a-z]{3}[0-9]{6}|[a-z]+\\.[a-z]+)@utdallas\\.edu$",
                message = "Email must match sequence (e.g., abc123456@utdallas.edu)"
        )
        String email,

        @NotNull(message = "Verification code is required")
        @Min(value = 100000, message = "Verification code must be 6 digits")
        @Max(value = 999999, message = "Verification code must be 6 digits")
        Integer verificationToken
) {}