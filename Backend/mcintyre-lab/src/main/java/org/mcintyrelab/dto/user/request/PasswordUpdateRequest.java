package org.mcintyrelab.dto.user.request;

public record PasswordUpdateRequest(
        String currentPassword,
        String newPassword
) {
}
