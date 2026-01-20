package com.tekion.API_Design_Demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Standard API response wrapper.
 * Success responses contain meta and data.
 * Error responses contain errors list.
 *
 * @param <T> Type of data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "Response metadata")
    private Meta meta;
    
    @Schema(description = "Response data payload")
    private T data;
    
    @Schema(description = "List of errors (for error responses)")
    private List<ApiError> errors;
    
    /**
     * Create a success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .meta(Meta.success())
                .data(data)
                .build();
    }
    
    /**
     * Create a success response with data and message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .meta(Meta.success(message))
                .data(data)
                .build();
    }
    
    /**
     * Create a success response with pagination
     */
    public static <T> ApiResponse<T> success(T data, int totalCount, int page, int pageSize) {
        return ApiResponse.<T>builder()
                .meta(Meta.success(totalCount, page, pageSize))
                .data(data)
                .build();
    }
    
    /**
     * Create an error response with single error
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .errors(List.of(ApiError.of(code, message)))
                .build();
    }
    
    /**
     * Create an error response with multiple errors
     */
    public static <T> ApiResponse<T> error(List<ApiError> errors) {
        return ApiResponse.<T>builder()
                .errors(errors)
                .build();
    }
}

