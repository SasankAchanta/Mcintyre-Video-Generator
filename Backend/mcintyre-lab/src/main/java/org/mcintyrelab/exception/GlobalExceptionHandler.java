package org.mcintyrelab.exception;

import org.mcintyrelab.exception.badrequest.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError error = new ApiError(ex.getMessage(), status.value(), status.getReasonPhrase(), LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}
