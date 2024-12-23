package com.vinn.personalBlog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application, providing centralized exception handling across all @RequestMapping methods.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles IllegalArgumentExceptions which can occur in various scenarios where arguments are deemed inappropriate.
     * @param ex the exception caught during controller operations.
     * @param request the current request.
     * @return a ResponseEntity object containing the error details and the HTTP status code.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", Collections.singletonList(ex.getLocalizedMessage()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles general exceptions that are not addressed by more specific handlers.
     * This acts as a catch-all for any other type of exception.
     * @param ex the exception caught during controller operations.
     * @param request the current request.
     * @return a ResponseEntity object containing the error details and the HTTP status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", Collections.singletonList("An unexpected error occurred: " + ex.getLocalizedMessage()));
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles ConstraintViolationException that occurs when a validation constraint is violated.
     * @param ex the ConstraintViolationException caught during validation of controller inputs.
     * @param request the current request.
     * @return a ResponseEntity containing the validation errors and the HTTP status code.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", ex.getConstraintViolations().stream()
                .map(cv -> cv.getRootBeanClass().getName() + " " + cv.getPropertyPath() + ": " + cv.getMessage())
                .toList());
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles exceptions when a requested category is not found.
     * @param ex the caught EntityNotFoundException.
     * @param request the current request.
     * @return a ResponseEntity object containing the error details and the HTTP status code.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleCategoryNotFoundException(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
