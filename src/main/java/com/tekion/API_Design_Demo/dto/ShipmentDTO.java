package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO classes for Shipment API
 */
public class ShipmentDTO {

    /**
     * Shipment Status Enum
     */
    public enum ShipmentStatus {
        PENDING,
        PICKED_UP,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        FAILED,
        RETURNED,
        CANCELLED
    }

    /**
     * Response metadata
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Response metadata")
    public static class Meta {
        @Schema(description = "Status of the response", example = "SUCCESS")
        private String status;
    }

    /**
     * Standard API Response wrapper
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Standard API response wrapper")
    public static class ApiResponse<T> {
        @Schema(description = "Response metadata")
        private Meta meta;

        @Schema(description = "Response data")
        private T data;

        public static <T> ApiResponse<T> success(T data) {
            return ApiResponse.<T>builder()
                    .meta(Meta.builder().status("SUCCESS").build())
                    .data(data)
                    .build();
        }
    }

    /**
     * Error detail
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error details")
    public static class ErrorDetail {
        @Schema(description = "Error code", example = "RESOURCE_NOT_FOUND")
        private String code;

        @Schema(description = "Error message", example = "Shipment not found")
        private String message;
    }

    /**
     * Error response
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error response")
    public static class ErrorResponse {
        @Schema(description = "List of errors")
        private List<ErrorDetail> errors;

        public static ErrorResponse of(String code, String message) {
            return ErrorResponse.builder()
                    .errors(List.of(ErrorDetail.builder().code(code).message(message).build()))
                    .build();
        }
    }

    /**
     * Shipment Response DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shipment details")
    public static class ShipmentResponse {
        @Schema(description = "Unique shipment identifier", example = "shp-550e8400-e29b-41d4-a716-446655440000")
        private String shipmentId;

        @Schema(description = "Associated fulfillment ID", example = "ful-550e8400-e29b-41d4-a716-446655440000")
        private String fulfillmentId;

        @Schema(description = "Shipping address ID", example = "addr-550e8400-e29b-41d4-a716-446655440000")
        private String addressId;

        @Schema(description = "Tracking number", example = "TRK1234567890ABC")
        private String trackingNumber;

        @Schema(description = "Carrier name", example = "FedEx")
        private String carrier;

        @Schema(description = "Shipment status")
        private ShipmentStatus status;

        @Schema(description = "Shipment creation timestamp")
        private LocalDateTime createdAt;

        @Schema(description = "Last update timestamp")
        private LocalDateTime updatedAt;

        @Schema(description = "Estimated delivery date")
        private LocalDateTime estimatedDeliveryDate;

        @Schema(description = "Actual delivery date")
        private LocalDateTime actualDeliveryDate;
    }

    /**
     * Create Shipment Request DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Request body for creating a new shipment")
    public static class CreateShipmentRequest {
        @NotBlank(message = "Fulfillment ID is required")
        @Schema(description = "Associated fulfillment ID", example = "ful-550e8400-e29b-41d4-a716-446655440000", required = true)
        private String fulfillmentId;

        @NotBlank(message = "Address ID is required")
        @Schema(description = "Shipping address ID", example = "addr-550e8400-e29b-41d4-a716-446655440000", required = true)
        private String addressId;

        @NotBlank(message = "Carrier is required")
        @Schema(description = "Carrier name", example = "FedEx", required = true)
        private String carrier;

        @Schema(description = "Estimated delivery date", example = "2026-01-25T14:00:00")
        private LocalDateTime estimatedDeliveryDate;
    }

    /**
     * Update Shipment Request DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Request body for updating a shipment")
    public static class UpdateShipmentRequest {
        @Schema(description = "Carrier name", example = "UPS")
        private String carrier;

        @Schema(description = "Shipment status")
        private ShipmentStatus status;

        @Schema(description = "Estimated delivery date")
        private LocalDateTime estimatedDeliveryDate;

        @Schema(description = "Actual delivery date")
        private LocalDateTime actualDeliveryDate;
    }

    /**
     * Update Shipment Status Request DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Request body for updating shipment status")
    public static class UpdateShipmentStatusRequest {
        @NotNull(message = "Status is required")
        @Schema(description = "New shipment status", required = true)
        private ShipmentStatus status;
    }
}

