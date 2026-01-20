package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response for API errors")
public class ErrorResponseDTO {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error code for client reference", example = "PRODUCT_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Human-readable error message", example = "Product with ID 'prod-999' not found")
    private String message;

    @Schema(description = "Detailed error description", example = "The requested product does not exist in the catalog. Please verify the product ID.")
    private String details;

    @Schema(description = "API path where error occurred", example = "/api/v1/products/prod-999")
    private String path;

    @Schema(description = "Timestamp when error occurred", example = "2025-01-20T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Field that caused the error (for validation errors)", example = "price")
    private String field;
}

