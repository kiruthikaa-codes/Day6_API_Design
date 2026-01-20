package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Review Data Transfer Object")
public class ReviewDTO {

    @Schema(description = "Unique review identifier", example = "REV001")
    private String reviewId;

    @Schema(description = "Customer who wrote the review", example = "CUST001")
    private String customerId;

    @Schema(description = "Product being reviewed", example = "PROD001")
    private String productId;

    @Schema(description = "Rating from 1 to 5", example = "5", minimum = "1", maximum = "5")
    private int rating;

    @Schema(description = "Review comment", example = "Excellent product!")
    private String comment;

    @Schema(description = "Date of review", example = "2026-01-20")
    private String reviewDate;
}

