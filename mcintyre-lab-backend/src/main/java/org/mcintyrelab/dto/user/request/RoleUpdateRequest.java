package org.mcintyrelab.dto.user.request;

import org.mcintyrelab.model.enums.Role;

import java.util.UUID;

public record RoleUpdateRequest(
        UUID targetUserId,
        Role newRole,
        String reason
) {
    public RoleUpdateRequest {
        // Intercept and fail fast if someone tries to assign the ADMIN role here
        if (Role.ROLE_ADMIN.equals(newRole)) {
            throw new IllegalArgumentException("Administrative roles must be assigned through the master console");
        }
    }
}
