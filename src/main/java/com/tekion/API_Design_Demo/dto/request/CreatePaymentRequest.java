package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for processing a payment")
public class CreatePaymentRequest {

    @NotBlank(message = "Order ID is required")
    @Schema(description = "Order ID to process payment for", example = "ord-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment method", example = "CREDIT_CARD", 
            allowableValues = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER", "CASH"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String method;
}

