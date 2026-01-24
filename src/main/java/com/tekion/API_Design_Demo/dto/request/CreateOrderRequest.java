package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new order. Note: Customer ID is obtained from the X-Customer-Id header (simulating authenticated user context)")
public class CreateOrderRequest {

    @NotEmpty(message = "At least one order item is required")
    @Schema(description = "List of items to order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemRequest> items;

    @Schema(description = "Shipping address ID", example = "addr-001")
    private String shippingAddressId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order item request")
    public static class OrderItemRequest {
        @NotBlank(message = "Product ID is required")
        @Schema(description = "Product ID to order", example = "prod-101", requiredMode = Schema.RequiredMode.REQUIRED)
        private String productId;

        @Schema(description = "Quantity to order", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer quantity;
    }
}

