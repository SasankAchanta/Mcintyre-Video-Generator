package org.mcintyrelab.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NameUpdateRequest (
        @NotNull(message = "Target user ID is required")
        UUID targetUserId,

        @NotBlank(message = "New name cannot be blank")
        @Size(min = 3, max = 150, message = "Combined name must be between 3 and 150 characters")
        @Pattern(regexp = "^\\S+\\s+\\S+.*$", message = "Name must contain both a first name and a last name separated by a space")
        String newName,

        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reason
) {
    public NameUpdateRequest {
        // Sanitize the input: strip out leading/trailing spaces
        // and shrink multiple inside spaces down to a single space
        if (newName != null) {
            newName = newName.trim().replaceAll("\\s+", " ");
        }

        // No "this.newName = newName;" needed! Java maps it automatically.
    }
}
