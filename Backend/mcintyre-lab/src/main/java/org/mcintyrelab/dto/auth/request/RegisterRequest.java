package org.mcintyrelab.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank @Pattern(regexp = "^[a-z]{3}[0-9]{6}@utdallas\\.edu$") String email,
        @NotBlank String username,
        @NotBlank String password
) {
}