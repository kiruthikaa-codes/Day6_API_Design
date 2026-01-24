package com.tekion.API_Design_Demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single error in the API response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error details")
public class ApiError {
    
    @Schema(description = "Error code", example = "RESOURCE_NOT_FOUND")
    private String code;
    
    @Schema(description = "Human-readable error message", example = "Inventory not found")
    private String message;
    
    @Schema(description = "Field that caused the error (for validation errors)", example = "quantity")
    private String field;
    
    /**
     * Create an error with code and message
     */
    public static ApiError of(String code, String message) {
        return ApiError.builder().code(code).message(message).build();
    }
    
    /**
     * Create an error with code, message, and field
     */
    public static ApiError of(String code, String message, String field) {
        return ApiError.builder().code(code).message(message).field(field).build();
    }
}

