package org.mcintyrelab.dto.user.request;

import java.util.UUID;

public record UsernameUpdateRequest (
        UUID targetUserId,
        String newUsername,
        String reason
){
}
