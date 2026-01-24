package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Review Data Transfer Object")
public class ReviewDTO {

    @Schema(description = "Unique review identifier (auto-generated)", example = "rev-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String reviewId;

    @Schema(description = "Customer ID who wrote the review", example = "cust-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerId;

    @Schema(description = "Customer name (populated from customer)", example = "John Doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerName;

    @Schema(description = "Product ID being reviewed", example = "prod-101", accessMode = Schema.AccessMode.READ_ONLY)
    private String productId;

    @Schema(description = "Product name (populated from product)", example = "Smartphone", accessMode = Schema.AccessMode.READ_ONLY)
    private String productName;

    @Schema(description = "Rating from 1 to 5", example = "5", minimum = "1", maximum = "5")
    private int rating;

    @Schema(description = "Review comment", example = "Excellent product!")
    private String comment;

    @Schema(description = "Date of review", example = "2026-01-20T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private java.time.LocalDateTime reviewDate;
}

