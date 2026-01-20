package com.tekion.API_Design_Demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Standard error response structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponse {
    
    @Schema(description = "List of errors")
    private List<ErrorDetail> errors;
    
    /**
     * Create an error response with a single error
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .errors(List.of(ErrorDetail.of(code, message)))
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
     * Individual error detail
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error detail")
    public static class ErrorDetail {
        
        @Schema(description = "Error code", example = "RESOURCE_NOT_FOUND")
        private String code;
        
        @Schema(description = "Error message", example = "Inventory not found")
        private String message;
        
        @Schema(description = "Field that caused the error (for validation errors)", example = "quantity")
        private String field;
        
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
    }
}

