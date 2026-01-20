package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.ShipmentDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "Shipments", description = "Shipment management API for tracking and managing shipments")
public class ShipmentController {

    // In-memory storage for shipments
    private final Map<String, ShipmentResponse> shipments = new ConcurrentHashMap<>();

    @PostMapping
    @Operation(summary = "Create a new shipment", description = "Creates a new shipment for a fulfillment order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Shipment created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {

        String shipmentId = "shp-" + UUID.randomUUID().toString();
        String trackingNumber = "TRK" + System.currentTimeMillis() +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        ShipmentResponse shipment = ShipmentResponse.builder()
                .shipmentId(shipmentId)
                .fulfillmentId(request.getFulfillmentId())
                .addressId(request.getAddressId())
                .trackingNumber(trackingNumber)
                .carrier(request.getCarrier())
                .status(ShipmentStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .estimatedDeliveryDate(request.getEstimatedDeliveryDate())
                .build();

        shipments.put(shipmentId, shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(shipment));
    }

    @GetMapping("/{shipmentId}")
    @Operation(summary = "Get shipment by ID", description = "Retrieves a specific shipment by its unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipment found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getShipmentById(
            @Parameter(description = "Unique shipment identifier", example = "shp-550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String shipmentId) {

        ShipmentResponse shipment = shipments.get(shipmentId);
        if (shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("RESOURCE_NOT_FOUND", "Shipment not found with id: " + shipmentId));
        }
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @GetMapping
    @Operation(summary = "Get all shipments", description = "Retrieves all shipments with optional filtering")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getAllShipments(
            @Parameter(description = "Filter by fulfillment ID") @RequestParam(required = false) String fulfillmentId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) ShipmentStatus status,
            @Parameter(description = "Filter by carrier") @RequestParam(required = false) String carrier) {

        List<ShipmentResponse> result = shipments.values().stream()
                .filter(s -> fulfillmentId == null || s.getFulfillmentId().equals(fulfillmentId))
                .filter(s -> status == null || s.getStatus() == status)
                .filter(s -> carrier == null || s.getCarrier().equalsIgnoreCase(carrier))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get shipment by tracking number", description = "Retrieves a shipment by its tracking number")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipment found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getShipmentByTrackingNumber(
            @Parameter(description = "Tracking number", example = "TRK1234567890ABC")
            @PathVariable String trackingNumber) {

        Optional<ShipmentResponse> shipment = shipments.values().stream()
                .filter(s -> s.getTrackingNumber().equals(trackingNumber))
                .findFirst();

        if (shipment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("RESOURCE_NOT_FOUND", "Shipment not found with tracking: " + trackingNumber));
        }
        return ResponseEntity.ok(ApiResponse.success(shipment.get()));
    }

    @PutMapping("/{shipmentId}")
    @Operation(summary = "Update a shipment", description = "Updates an existing shipment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipment updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateShipment(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId,
            @Valid @RequestBody UpdateShipmentRequest request) {

        ShipmentResponse existing = shipments.get(shipmentId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("RESOURCE_NOT_FOUND", "Shipment not found with id: " + shipmentId));
        }

        ShipmentResponse updated = ShipmentResponse.builder()
                .shipmentId(existing.getShipmentId())
                .fulfillmentId(existing.getFulfillmentId())
                .addressId(existing.getAddressId())
                .trackingNumber(existing.getTrackingNumber())
                .carrier(request.getCarrier() != null ? request.getCarrier() : existing.getCarrier())
                .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .estimatedDeliveryDate(request.getEstimatedDeliveryDate() != null ?
                        request.getEstimatedDeliveryDate() : existing.getEstimatedDeliveryDate())
                .actualDeliveryDate(request.getActualDeliveryDate() != null ?
                        request.getActualDeliveryDate() : existing.getActualDeliveryDate())
                .build();

        shipments.put(shipmentId, updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PatchMapping("/{shipmentId}/status")
    @Operation(summary = "Update shipment status", description = "Updates only the status of a shipment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateShipmentStatus(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId,
            @Valid @RequestBody UpdateShipmentStatusRequest request) {

        ShipmentResponse existing = shipments.get(shipmentId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("RESOURCE_NOT_FOUND", "Shipment not found with id: " + shipmentId));
        }

        LocalDateTime actualDelivery = existing.getActualDeliveryDate();
        if (request.getStatus() == ShipmentStatus.DELIVERED && actualDelivery == null) {
            actualDelivery = LocalDateTime.now();
        }

        ShipmentResponse updated = ShipmentResponse.builder()
                .shipmentId(existing.getShipmentId())
                .fulfillmentId(existing.getFulfillmentId())
                .addressId(existing.getAddressId())
                .trackingNumber(existing.getTrackingNumber())
                .carrier(existing.getCarrier())
                .status(request.getStatus())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .estimatedDeliveryDate(existing.getEstimatedDeliveryDate())
                .actualDeliveryDate(actualDelivery)
                .build();

        shipments.put(shipmentId, updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @GetMapping("/{shipmentId}/tracking")
    @Operation(summary = "Track shipment", description = "Get tracking information for a specific shipment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tracking info retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> trackShipment(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId) {

        ShipmentResponse shipment = shipments.get(shipmentId);
        if (shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("RESOURCE_NOT_FOUND", "Shipment not found with id: " + shipmentId));
        }

        // Return tracking info (same as shipment details for now, could be a separate DTO)
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }
}

