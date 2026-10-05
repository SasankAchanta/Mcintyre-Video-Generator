package org.mcintyrelab.dto.video;

import org.mcintyrelab.model.enums.VideoType;
import java.time.LocalDateTime;
import java.util.UUID;

public record VideoDto(
        UUID videoId,
        String videoUrl,
        VideoType videoType,
        LocalDateTime createdAt
) {}