package org.mcintyrelab.dto;

import java.util.List;

public record CreateUntrainedRequest(
        Integer userId,
        List<String> videoUrl
) {
}
