package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Payment Data Transfer Object")
public class PaymentDTO {

    @Schema(description = "Unique payment identifier",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID paymentId = UUID.randomUUID();

    @Schema(description = "Order identifier associated with this payment",
            example = "123e4567-e89b-12d3-a456-426614174001",
            required = true)
    private UUID orderId;

    @Schema(description = "Payment amount",
            example = "99.99",
            required = true,
            minimum = "0")
    private double amount;

    @Schema(description = "Payment method used",
            example = "CREDIT_CARD",
            required = true)
    private String method;

    @Schema(description = "Current payment status",
            example = "COMPLETED",
            required = true)
    private String status;

    public PaymentDTO(UUID orderId, double amount, String method, String status){
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
