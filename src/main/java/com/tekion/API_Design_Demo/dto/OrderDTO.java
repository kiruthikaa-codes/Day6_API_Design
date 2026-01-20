package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Data Transfer Object representing an Order")
public class OrderDTO {

    @Schema(description = "Unique identifier of the order", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID orderId;

    @Schema(description = "Unique identifier of the customer who placed the order", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID customerId;

    @Schema(description = "Date and time when the order was placed", example = "2024-01-15T10:30:00")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status;

    @Schema(description = "Total amount of the order", example = "299.99", minimum = "0")
    private BigDecimal totalAmount;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}

