package com.tekion.API_Design_Demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentDTO {

    private String fulfillmentId;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Status is required")
    private FulfillmentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum as inner class - keeps it simple, no extra file
    public enum FulfillmentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCELLED
    }
}