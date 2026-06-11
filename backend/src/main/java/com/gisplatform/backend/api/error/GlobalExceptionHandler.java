package com.gisplatform.backend.api.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ApiFieldError> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiFieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<ApiFieldError> details = exception.getConstraintViolations()
                .stream()
                .map(violation -> new ApiFieldError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "Malformed request body", List.of(), request);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleErrorResponse(
            ErrorResponseException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getBody().getDetail();

        return build(status, message != null ? message : status.getReasonPhrase(), List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", List.of(), request);
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String message,
            List<ApiFieldError> details,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );

        return ResponseEntity.status(status).body(body);
    }
}
