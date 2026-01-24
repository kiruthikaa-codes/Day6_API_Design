package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Fulfillment Data Transfer Object representing order fulfillment status")
public class FulfillmentDTO {

    @Schema(description = "Unique fulfillment identifier (auto-generated)", example = "ful-12345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String fulfillmentId;

    @NotBlank(message = "Order ID is required")
    @Schema(description = "Order ID associated with this fulfillment", example = "ord-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @NotNull(message = "Status is required")
    @Schema(description = "Current fulfillment status", example = "PENDING",
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "CANCELLED"})
    private FulfillmentStatus status;

    @Schema(description = "Timestamp when fulfillment was created", example = "2026-01-20T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when fulfillment was last updated", example = "2026-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    // Enum as inner class - keeps it simple, no extra file
    @Schema(description = "Fulfillment status values")
    public enum FulfillmentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCELLED
    }
}