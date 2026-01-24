package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order Item representing a product in an order")
public class OrderItemDTO {

    @Schema(description = "Unique identifier of the order item", example = "item-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String orderItemId;

    @Schema(description = "Product ID being ordered", example = "prod-101", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productId;

    @Schema(description = "Product name (populated from product)", example = "Smartphone", accessMode = Schema.AccessMode.READ_ONLY)
    private String productName;

    @Schema(description = "Quantity ordered", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "Unit price at time of order (from product)", example = "699.99", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal unitPrice;

    @Schema(description = "Total price for this item (quantity * unitPrice)", example = "1399.98", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalPrice;
}

