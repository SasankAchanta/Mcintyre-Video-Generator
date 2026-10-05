package org.mcintyrelab.dto.user.request;

import jakarta.validation.constraints.PastOrPresent;
import org.mcintyrelab.model.enums.Role;

import java.time.YearMonth;

public record AllUsersRequest (
        //  Your custom filters
        Role role,

        @PastOrPresent(message = "The cutoff month cannot be a future date")
        YearMonth cutoffMonth
){
}
