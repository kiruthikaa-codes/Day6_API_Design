package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment Data Transfer Object")
public class PaymentDTO {

    @Schema(description = "Unique payment identifier (auto-generated)",
            example = "pay-001",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String paymentId;

    @Schema(description = "Order ID associated with this payment",
            example = "ord-001",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String orderId;

    @Schema(description = "Payment amount (from order total)",
            example = "299.99",
            accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal amount;

    @Schema(description = "Payment method used",
            example = "CREDIT_CARD",
            allowableValues = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER", "CASH"})
    private String method;

    @Schema(description = "Current payment status",
            example = "COMPLETED",
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "FAILED", "REFUNDED", "CANCELLED"})
    private String status;

    @Schema(description = "Transaction reference number", example = "TXN-123456", accessMode = Schema.AccessMode.READ_ONLY)
    private String transactionRef;

    @Schema(description = "Timestamp when payment was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when payment was last updated", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
