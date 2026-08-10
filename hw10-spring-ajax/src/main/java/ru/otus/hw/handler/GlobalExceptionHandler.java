package ru.otus.hw.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.otus.hw.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());

        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing + ", " + replacement
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred. Exception type: {}, Message: {}",
                e.getClass().getSimpleName(), e.getMessage(), e);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "An unexpected error occurred. Please try again later."
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        error,
                        message,
                        LocalDateTime.now()
                ));
    }

    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}

    public record ValidationErrorResponse(
            int status,
            String error,
            Map<String, String> errors,
            LocalDateTime timestamp
    ) {}
}