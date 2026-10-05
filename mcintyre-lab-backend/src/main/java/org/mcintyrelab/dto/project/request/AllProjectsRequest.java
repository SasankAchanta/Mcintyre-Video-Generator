package org.mcintyrelab.dto.project.request;

import jakarta.validation.constraints.PastOrPresent;
import org.mcintyrelab.model.enums.ModelType;

import java.time.YearMonth;

public record AllProjectsRequest(
        // Custom filters for your workspace
        ModelType modelType,

        @PastOrPresent(message = "The cutoff month cannot be a future date")
        YearMonth cutoffMonth
) {
}