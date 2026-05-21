package org.mcintyrelab.dto.user.request;

import org.mcintyrelab.model.enums.Role;

import java.time.YearMonth;

public record AllUsersRequest (
        //  Your custom filters
        Role role,
        YearMonth cutoffMonth
){
}
