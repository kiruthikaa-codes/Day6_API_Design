package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new review. Note: Customer ID is obtained from the X-Customer-Id header (simulating authenticated user context)")
public class CreateReviewRequest {

    @NotBlank(message = "Product ID is required")
    @Schema(description = "Product being reviewed", example = "prod-101", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Rating from 1 to 5", example = "5", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer rating;

    @Schema(description = "Review comment", example = "Excellent product!")
    private String comment;
}

