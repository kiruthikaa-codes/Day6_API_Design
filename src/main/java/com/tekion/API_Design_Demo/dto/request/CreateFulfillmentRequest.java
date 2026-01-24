package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new fulfillment.
 * Only orderId is required - fulfillmentId and timestamps are auto-generated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new fulfillment")
public class CreateFulfillmentRequest {

    @NotBlank(message = "Order ID is required")
    @Schema(description = "Order ID to create fulfillment for", example = "ord-12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;
}

