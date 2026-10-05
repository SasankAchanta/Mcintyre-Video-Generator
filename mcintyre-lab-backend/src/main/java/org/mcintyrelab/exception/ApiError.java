package org.mcintyrelab.exception;

import java.time.LocalDateTime;

public record ApiError (
        String message,
        int statusCode,
        String errorPhrase,
        LocalDateTime timestamp
){
}
