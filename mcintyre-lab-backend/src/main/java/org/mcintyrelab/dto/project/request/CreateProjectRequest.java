package org.mcintyrelab.dto.project.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mcintyrelab.model.enums.ModelType;

public record CreateProjectRequest (
        @NotNull(message = "Model type is required")
        ModelType modelType,

        @NotBlank(message = "Project name cannot be blank")
        @Size(min = 1, max = 100, message = "Project name must be between 1 and 100 characters")
        String projectName,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description
){}
