package com.tekion.API_Design_Demo.exception;

import com.tekion.API_Design_Demo.dto.response.ErrorResponse;
import com.tekion.API_Design_Demo.dto.response.ErrorResponse.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the API.
 * Catches exceptions and returns user-friendly, detailed error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations.
     * Returns detailed field-level error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        List<ErrorDetail> errorDetails = new ArrayList<>();
        
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            ErrorDetail detail = ErrorDetail.builder()
                    .code("VALIDATION_ERROR")
                    .message(fieldError.getDefaultMessage())
                    .field(fieldError.getField())
                    .reason("Field '" + fieldError.getField() + "' failed validation")
                    .suggestion("Please provide a valid value for '" + fieldError.getField() + "'")
                    .build();
            errorDetails.add(detail);
        }
        
        ErrorResponse response = ErrorResponse.builder()
                .errors(errorDetails)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle malformed JSON or unreadable request body.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        String message = "Invalid request body. Please check your JSON format.";
        String reason = ex.getMostSpecificCause().getMessage();
        
        // Extract a cleaner error message
        if (reason != null && reason.contains("Cannot deserialize")) {
            message = "Invalid field value in request body.";
        } else if (reason != null && reason.contains("Unexpected character")) {
            message = "Malformed JSON. Please check for syntax errors.";
        }
        
        ErrorDetail detail = ErrorDetail.builder()
                .code("INVALID_REQUEST_BODY")
                .message(message)
                .reason(reason != null ? reason.split("\n")[0] : "Unable to parse request body")
                .suggestion("Ensure the request body is valid JSON with correct field types")
                .build();
        
        ErrorResponse response = ErrorResponse.builder()
                .errors(List.of(detail))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle missing required request headers.
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(
            MissingRequestHeaderException ex, WebRequest request) {
        
        ErrorDetail detail = ErrorDetail.builder()
                .code("MISSING_HEADER")
                .message("Required header '" + ex.getHeaderName() + "' is missing")
                .field(ex.getHeaderName())
                .reason("The request is missing a required header")
                .suggestion("Add the '" + ex.getHeaderName() + "' header to your request")
                .build();
        
        ErrorResponse response = ErrorResponse.builder()
                .errors(List.of(detail))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle type mismatch errors (e.g., string instead of number in path variable).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        String paramName = ex.getName();
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        
        ErrorDetail detail = ErrorDetail.builder()
                .code("TYPE_MISMATCH")
                .message("Invalid value for parameter '" + paramName + "'")
                .field(paramName)
                .reason("Expected type: " + expectedType + ", but got: " + ex.getValue())
                .suggestion("Please provide a valid " + expectedType + " value for '" + paramName + "'")
                .build();
        
        ErrorResponse response = ErrorResponse.builder()
                .errors(List.of(detail))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        ErrorDetail detail = ErrorDetail.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .reason(ex.getMessage())
                .suggestion("Please try again or contact support if the problem persists")
                .build();
        
        ErrorResponse response = ErrorResponse.builder()
                .errors(List.of(detail))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

