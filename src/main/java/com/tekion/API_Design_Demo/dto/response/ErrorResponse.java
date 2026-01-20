package com.tekion.API_Design_Demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure with detailed, user-friendly error information.
 * Provides clear error codes, descriptive messages, and actionable suggestions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response with detailed error information")
public class ErrorResponse {

    @Schema(description = "List of errors that occurred", example = "[{\"code\": \"RESOURCE_NOT_FOUND\", \"message\": \"Inventory record not found\"}]")
    private List<ErrorDetail> errors;

    @Schema(description = "Timestamp when the error occurred", example = "2024-01-20T14:30:00")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Request path that caused the error", example = "/api/inventory/inv-123")
    private String path;

    /**
     * Create an error response with a single error
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .errors(List.of(ErrorDetail.of(code, message)))
                .build();
    }

    /**
     * Create an error response with a single error and path
     */
    public static ErrorResponse of(String code, String message, String path) {
        return ErrorResponse.builder()
                .errors(List.of(ErrorDetail.of(code, message)))
                .path(path)
                .build();
    }

    /**
     * Create an error response with multiple errors
     */
    public static ErrorResponse of(List<ErrorDetail> errors) {
        return ErrorResponse.builder()
                .errors(errors)
                .build();
    }

    /**
     * Individual error detail with comprehensive information for debugging and resolution.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detailed error information")
    public static class ErrorDetail {

        @Schema(
            description = "Unique error code for programmatic handling. Common codes:\n" +
                          "• RESOURCE_NOT_FOUND - The requested resource does not exist\n" +
                          "• VALIDATION_ERROR - Request data failed validation\n" +
                          "• DUPLICATE_RESOURCE - Resource already exists (conflict)\n" +
                          "• INSUFFICIENT_STOCK - Not enough inventory for the operation\n" +
                          "• CAPACITY_EXCEEDED - Operation would exceed maximum capacity\n" +
                          "• INVALID_STATUS_TRANSITION - Invalid inventory status change\n" +
                          "• MISSING_REQUIRED_FIELD - A required field was not provided",
            example = "RESOURCE_NOT_FOUND"
        )
        private String code;

        @Schema(
            description = "Human-readable error message explaining what went wrong",
            example = "Inventory record with ID 'inv-123e4567-e89b-12d3-a456-426614174000' was not found"
        )
        private String message;

        @Schema(
            description = "The specific field that caused the error (for validation errors)",
            example = "quantity"
        )
        private String field;

        @Schema(
            description = "The invalid value that was provided (for validation errors)",
            example = "-10"
        )
        private String rejectedValue;

        @Schema(
            description = "Detailed explanation of why the error occurred",
            example = "The inventory ID format is invalid. Expected format: inv-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
        )
        private String reason;

        @Schema(
            description = "Suggested action to resolve the error",
            example = "Verify the inventory ID exists by calling GET /api/inventory first, or create a new inventory record using POST /api/inventory"
        )
        private String suggestion;

        public static ErrorDetail of(String code, String message) {
            return ErrorDetail.builder()
                    .code(code)
                    .message(message)
                    .build();
        }

        public static ErrorDetail of(String code, String message, String field) {
            return ErrorDetail.builder()
                    .code(code)
                    .message(message)
                    .field(field)
                    .build();
        }

        public static ErrorDetail withSuggestion(String code, String message, String reason, String suggestion) {
            return ErrorDetail.builder()
                    .code(code)
                    .message(message)
                    .reason(reason)
                    .suggestion(suggestion)
                    .build();
        }

        public static ErrorDetail validationError(String field, String message, String rejectedValue, String suggestion) {
            return ErrorDetail.builder()
                    .code("VALIDATION_ERROR")
                    .message(message)
                    .field(field)
                    .rejectedValue(rejectedValue)
                    .suggestion(suggestion)
                    .build();
        }
    }
}

