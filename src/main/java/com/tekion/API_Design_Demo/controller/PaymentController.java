package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.OrderDTO;
import com.tekion.API_Design_Demo.dto.PaymentDTO;
import com.tekion.API_Design_Demo.dto.request.CreatePaymentRequest;
import com.tekion.API_Design_Demo.dto.response.ApiResponse;
import com.tekion.API_Design_Demo.service.DataStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payment Management", description = "APIs for managing payment processing, status updates, and refunds")
public class PaymentController {

    private final DataStore dataStore;

    public PaymentController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Operation(summary = "List all payments", description = "Retrieve a list of all payments with optional filtering")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list of payments")
    })
    @GetMapping(value = "/payments", produces = "application/json")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPayments(
            @Parameter(description = "Filter by order ID") @RequestParam(required = false) String orderId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {

        List<PaymentDTO> payments = dataStore.getPayments().values().stream()
                .filter(p -> orderId == null || orderId.equals(p.getOrderId()))
                .filter(p -> status == null || status.equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @Operation(summary = "Get payment details", description = "Retrieve detailed information about a specific payment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping(value = "/payments/{paymentId}", produces = "application/json")
    public ResponseEntity<?> getPaymentById(
            @Parameter(description = "Unique payment identifier", required = true)
            @PathVariable String paymentId) {
        PaymentDTO payment = dataStore.getPayment(paymentId);
        if (payment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Payment not found with id: " + paymentId));
        }
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @Operation(summary = "Process new payment", description = "Create and process a new payment for an order. Amount is derived from order total.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment processed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payment data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping(value = "/payments", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> processPayment(@Valid @RequestBody CreatePaymentRequest request) {
        // Validate order exists and get amount from order
        OrderDTO order = dataStore.getOrder(request.getOrderId());
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Order not found with id: " + request.getOrderId()));
        }

        String paymentId = "pay-" + UUID.randomUUID().toString().substring(0, 8);
        String transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        PaymentDTO payment = PaymentDTO.builder()
                .paymentId(paymentId)
                .orderId(order.getOrderId())
                .amount(order.getTotalAmount())  // Amount comes from order, not user input
                .method(request.getMethod())
                .status("PENDING")
                .transactionRef(transactionRef)
                .createdAt(now)
                .updatedAt(now)
                .build();

        dataStore.savePayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(payment));
    }

    @Operation(summary = "Update payment status", description = "Update the status of an existing payment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment status updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PatchMapping(value = "/payments/{paymentId}/status", produces = "application/json")
    public ResponseEntity<?> updatePaymentStatus(
            @Parameter(description = "Unique payment identifier", required = true)
            @PathVariable String paymentId,
            @Parameter(description = "New status", required = true)
            @RequestParam String status) {

        PaymentDTO existing = dataStore.getPayment(paymentId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Payment not found with id: " + paymentId));
        }

        PaymentDTO updated = PaymentDTO.builder()
                .paymentId(existing.getPaymentId())
                .orderId(existing.getOrderId())
                .amount(existing.getAmount())
                .method(existing.getMethod())
                .status(status)
                .transactionRef(existing.getTransactionRef())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        dataStore.savePayment(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "Refund payment", description = "Process a refund for an existing payment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment refunded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment cannot be refunded")
    })
    @PostMapping(value = "/payments/{paymentId}/refund", produces = "application/json")
    public ResponseEntity<?> refundPayment(
            @Parameter(description = "Unique payment identifier", required = true)
            @PathVariable String paymentId) {

        PaymentDTO existing = dataStore.getPayment(paymentId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Payment not found with id: " + paymentId));
        }

        if (!"COMPLETED".equals(existing.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("INVALID_STATE", "Only completed payments can be refunded"));
        }

        PaymentDTO refunded = PaymentDTO.builder()
                .paymentId(existing.getPaymentId())
                .orderId(existing.getOrderId())
                .amount(existing.getAmount())
                .method(existing.getMethod())
                .status("REFUNDED")
                .transactionRef(existing.getTransactionRef())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        dataStore.savePayment(refunded);
        return ResponseEntity.ok(ApiResponse.success(refunded));
    }

    @Operation(summary = "Check payment status", description = "Retrieve the current status of a payment")
    @GetMapping(value = "/payments/{paymentId}/status", produces = "application/json")
    public ResponseEntity<?> checkPaymentStatus(
            @Parameter(description = "Unique payment identifier", required = true)
            @PathVariable String paymentId) {

        PaymentDTO payment = dataStore.getPayment(paymentId);
        if (payment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Payment not found with id: " + paymentId));
        }
        return ResponseEntity.ok(ApiResponse.success(payment.getStatus()));
    }

    @Operation(summary = "Get payments for order", description = "Retrieve all payments for a specific order")
    @GetMapping(value = "/orders/{orderId}/payments", produces = "application/json")
    public ResponseEntity<?> getPaymentsForOrder(
            @Parameter(description = "Unique order identifier", required = true)
            @PathVariable String orderId) {

        OrderDTO order = dataStore.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Order not found with id: " + orderId));
        }

        List<PaymentDTO> payments = dataStore.getPayments().values().stream()
                .filter(p -> orderId.equals(p.getOrderId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(payments));
    }
}