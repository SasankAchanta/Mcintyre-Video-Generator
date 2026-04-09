package org.mcintyrelab.dto;

import jakarta.validation.constraints.NotBlank;


public record CreateUser(
        @NotBlank String username,
        @NotBlank String password
) {
}
