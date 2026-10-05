package org.mcintyrelab.dto.project.response;

import org.mcintyrelab.dto.project.ProjectDto;
import org.springframework.data.domain.Page;

public record AllProjectsResponse(
        Page<ProjectDto> projectPage
) {
}
