package org.mcintyrelab.dto.user.response;


import org.mcintyrelab.dto.user.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record AllUsersResponse(
        List<UserDto> users,
        int currentPage,
        int totalPages,
        long totalElements
) {
    // A beautiful helper constructor to map a Spring Page directly to this DTO
    public AllUsersResponse(Page<UserDto> userPage) {
        this(
                userPage.getContent(),      // The list of 20 users for the current page
                userPage.getNumber() + 1, // OPTIONAL FIX: Converts 0-indexed to 1-indexed for your UI                userPage.getTotalPages(),   // e.g., 5 total pages exist
                userPage.getTotalPages(),   // e.g., 5 total pages exist
                userPage.getTotalElements() // e.g., 98 total matching users in the database
        );
    }
}
