package org.mcintyrelab.dto.project.request;

import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @Size(min = 1, max = 100, message = "Project name must be between 1 and 100 characters")
        String projectName, // Can be null if only description changed

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description  // Can be null if only title changed
) {}