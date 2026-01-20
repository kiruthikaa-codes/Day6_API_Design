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
@Schema(description = "Data Transfer Object for Product information")
public class ProductDTO {

    @Schema(description = "Unique product ID (auto-generated)", example = "prod-101", accessMode = Schema.AccessMode.READ_ONLY)
    private String productId;

    @Schema(description = "Product name", example = "Smartphone", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Product description", example = "Latest model smartphone with advanced features")
    private String description;

    @Schema(description = "Product price in USD (must be greater than 0)", example = "699.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "Product category", example = "electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    @Schema(description = "Product stock quantity", example = "150")
    private Integer stockQuantity;

    @Schema(description = "Whether the product is active", example = "true")
    private Boolean isActive;

    // Audit Fields
    @Schema(description = "User who created the product", example = "admin@tekion.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(description = "Timestamp when product was created", example = "2025-01-20T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "User who last updated the product", example = "manager@tekion.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    @Schema(description = "Timestamp when product was last updated", example = "2025-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

