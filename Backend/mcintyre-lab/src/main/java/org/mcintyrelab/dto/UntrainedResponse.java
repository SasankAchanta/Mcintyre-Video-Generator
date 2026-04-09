package org.mcintyrelab.dto;

import java.time.LocalDateTime;

public record UntrainedResponse (
        String videoUrl,
        Boolean isTrained,
        LocalDateTime createdAt
) {
}
