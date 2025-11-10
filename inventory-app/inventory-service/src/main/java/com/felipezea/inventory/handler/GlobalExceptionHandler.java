package com.felipezea.inventory.handler;

import exception.EntityDuplicateException;
import exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    /**
     * Creates the unified JSON:API error response wrapper.
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, List<Map<String, Object>> errors) {
        final Map<String, Object> errorMap = Map.of("errors", errors);
        log.error("[GlobalExceptionHandler] Response Status: {}", status.value());
        return ResponseEntity.status(status).body(errorMap);
    }

    /**
     * Creates a single JSON:API error object.
     */
    private Map<String, Object> createSingleError(String status, String title, String detail) {
        return Map.of("status", status, "title", title, "detail", detail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(final EntityNotFoundException ex)
    {
        final Map<String, Object> error = createSingleError(
                "404",
                "Not Found",
                ex.getMessage() != null ? ex.getMessage() : "The entity does not exist"
        );
        log.error("[EntityNotFoundException] exception: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.NOT_FOUND, List.of(error));
    }

    @ExceptionHandler(EntityDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleEntityDuplicateException(final EntityDuplicateException ex)
    {
        final Map<String, Object> error = createSingleError(
                "400",
                "Duplicate Value",
                ex.getMessage() != null ? ex.getMessage() : "The entity was already created"
        );
        log.error("[EntityDuplicateException] exception: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, List.of(error));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(TypeMismatchException ex)
    {
        final String detail = ex.getRequiredType() == null
                ? String.format("The value '$%s' is not a correct type", ex.getRequiredType())
                : String.format("The value '%s' is not of type '%s'.", ex.getValue(), ex.getRequiredType());

        final Map<String, Object> error = createSingleError(
                "400",
                "Type Mismatch",
                detail
        );
        log.error("[TypeMismatchException] exception: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, List.of(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex)
    {
        // Convert all field errors into a list of JSON:API error objects
        final List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "status", "400",
                        "title", "Validation Error",
                        "detail", fieldError.getDefaultMessage(),
                        "source", Map.of("pointer", "/data/attributes/" + fieldError.getField()) // JSON:API pointer to field
                )).toList();

        log.error("[MethodArgumentNotValidException] exception: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)
    {
        final Map<String, Object> error = createSingleError(
                "400",
                "Malformed JSON",
                "The request body could not be read or is malformed."
        );
        log.error("[HttpMessageNotReadableException] exception: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, List.of(error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(final IllegalArgumentException ex)
    {
        final Map<String, Object> error = createSingleError(
                "400",
                "Invalid Argument",
                ex.getMessage()
        );
        log.error("[IllegalArgumentException] exception: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, List.of(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex)
    {
        final Map<String, Object> error = createSingleError(
                "500",
                "Fatal Error",
                ex.getMessage().isBlank() ? "Something wrong happened" : ex.getMessage()
        );
        log.error("[Exception] exception: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, List.of(error));
    }
}
