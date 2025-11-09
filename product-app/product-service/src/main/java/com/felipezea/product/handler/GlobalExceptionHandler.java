package com.felipezea.product.handler;

import com.felipezea.exception.EntityDuplicateException;
import com.felipezea.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(final EntityNotFoundException ex)
    {
        final Map<String, Object> error = Map
                .of("errors",
                    List.of(Map.of(
                        "status", "404",
                        "title", "Not Found",
                        "detail", ex.getMessage() != null ? ex.getMessage() : "The entity does not exist"
                    )));

        log.error("[EntityNotFoundException] exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EntityDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleEntityDuplicateException(final EntityDuplicateException ex)
    {
        final Map<String, Object> error = Map
                .of("errors",
                        List.of(Map.of(
                                "status", "404",
                                "title", "Not Found",
                                "detail", ex.getMessage() != null ? ex.getMessage() : "The entity was already created"
                        )));

        log.error("[EntityDuplicateException] exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(TypeMismatchException ex)
    {
        final Map<String, Object> error = Map.of(
                "error", ex.getRequiredType() == null
                        ? String.format("The value '$%s' is not a correct type", ex.getRequiredType())
                        : String.format("The value '%s' is not of type '%s'.", ex.getValue(), ex.getRequiredType())
        );

        log.error("[TypeMismatchException] exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex)
    {
        Map<String, Object> error = Map.of(
                "status", 400,
                "errors",
                ex.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList()

        );

        log.error("[MethodArgumentNotValidException] exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)
    {
        Map<String, Object> error = Map.of(
                "status", 400,
                "error", ex.getMessage()
        );

        log.error("[HttpMessageNotReadableException] exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones lanzadas manualmente (ej. IllegalArgumentException).
     * Retorna una respuesta JSON:API con un único objeto de error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(final IllegalArgumentException ex)
    {
        final Map<String, Object> error = Map.of(
                "errors",
                List.of(createSingleError("400", "Invalid Argument", ex.getMessage()))
        );

        log.error("[IllegalArgumentException] exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private Map<String, Object> createSingleError(final String status, final String title, final String detail) {
        return Map.of("errors", List.of(Map.of("status", status, "title", title, "detail", detail)));
    }
}
