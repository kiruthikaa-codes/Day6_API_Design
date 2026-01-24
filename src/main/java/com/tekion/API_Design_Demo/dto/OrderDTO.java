package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing an Order")
public class OrderDTO {

    @Schema(description = "Unique identifier of the order (auto-generated)", example = "ord-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String orderId;

    @Schema(description = "Customer ID who placed the order", example = "cust-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerId;

    @Schema(description = "Customer name (populated from customer)", example = "John Doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerName;

    @Schema(description = "List of items in the order", accessMode = Schema.AccessMode.READ_ONLY)
    private List<OrderItemDTO> items;

    @Schema(description = "Date and time when the order was placed", example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order", example = "PENDING",
            allowableValues = {"PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status;

    @Schema(description = "Total amount of the order (calculated from items)", example = "299.99", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalAmount;

    @Schema(description = "Shipping address ID", example = "addr-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String shippingAddressId;

    @Schema(description = "Timestamp when order was created", example = "2025-01-20T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when order was last updated", example = "2025-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

