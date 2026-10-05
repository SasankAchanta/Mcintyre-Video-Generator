package org.mcintyrelab.dto.project;

import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.video.VideoDto;
import org.mcintyrelab.model.enums.ModelType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProjectDto(
        UUID projectId,
        UserDto user,
        ModelType modelType,
        String projectName,
        String description,
        List<VideoDto> videos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
