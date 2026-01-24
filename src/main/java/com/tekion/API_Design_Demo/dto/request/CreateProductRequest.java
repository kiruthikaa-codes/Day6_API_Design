package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new product")
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Schema(description = "Product name", example = "Smartphone", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Product description", example = "Latest model smartphone with advanced features")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Schema(description = "Product price in USD", example = "699.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @NotBlank(message = "Category is required")
    @Schema(description = "Product category", example = "electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    @Schema(description = "Initial stock quantity", example = "150", minimum = "0")
    private Integer stockQuantity;
}

