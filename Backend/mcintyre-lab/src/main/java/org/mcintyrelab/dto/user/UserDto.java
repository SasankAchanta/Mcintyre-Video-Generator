package org.mcintyrelab.dto.user;

import org.mcintyrelab.model.enums.Role;

import java.time.YearMonth;

public record UserDto (
        String firstName,
        String lastName,
        String profilePicture,
        String email,
        Role role,
        YearMonth dateJoined
){
}
