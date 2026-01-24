package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.AddressDTO;
import com.tekion.API_Design_Demo.dto.FulfillmentDTO;
import com.tekion.API_Design_Demo.dto.ShipmentDTO.*;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "Shipments", description = "Shipment management API for tracking and managing shipments")
public class ShipmentController {

    private final DataStore dataStore;

    public ShipmentController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @GetMapping
    @Operation(summary = "Get all shipments", description = "Retrieves all shipments with optional filtering")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipments retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getAllShipments(
            @Parameter(description = "Filter by fulfillment ID") @RequestParam(required = false) String fulfillmentId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) ShipmentStatus status,
            @Parameter(description = "Filter by carrier") @RequestParam(required = false) String carrier,
            @Parameter(description = "Search by tracking number (partial match)") @RequestParam(required = false) String trackingSearch) {

        List<ShipmentResponse> result = dataStore.getShipments().values().stream()
                .filter(s -> fulfillmentId == null || s.getFulfillmentId().equals(fulfillmentId))
                .filter(s -> status == null || s.getStatus() == status)
                .filter(s -> carrier == null || s.getCarrier().equalsIgnoreCase(carrier))
                .filter(s -> trackingSearch == null || s.getTrackingNumber().toLowerCase().contains(trackingSearch.toLowerCase()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{shipmentId}")
    @Operation(summary = "Get shipment by ID", description = "Retrieves a specific shipment by its unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipment found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<?> getShipmentById(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId) {

        ShipmentResponse shipment = dataStore.getShipment(shipmentId);
        if (shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Shipment not found with id: " + shipmentId));
        }
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get shipment by tracking number", description = "Retrieves a shipment by its tracking number")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipment found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<?> getShipmentByTrackingNumber(
            @Parameter(description = "Tracking number") @PathVariable String trackingNumber) {

        Optional<ShipmentResponse> shipment = dataStore.getShipments().values().stream()
                .filter(s -> s.getTrackingNumber().equals(trackingNumber))
                .findFirst();

        if (shipment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Shipment not found with tracking: " + trackingNumber));
        }
        return ResponseEntity.ok(ApiResponse.success(shipment.get()));
    }

    @GetMapping("/{shipmentId}/tracking")
    @Operation(summary = "Track shipment", description = "Get tracking information for a specific shipment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tracking info retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<?> trackShipment(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId) {

        ShipmentResponse shipment = dataStore.getShipment(shipmentId);
        if (shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Shipment not found with id: " + shipmentId));
        }

        // Return tracking info (same as shipment details for now, could be a separate DTO)
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PostMapping
    @Operation(summary = "Create a new shipment", description = "Creates a new shipment for a fulfillment. Validates that fulfillment and address exist.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Shipment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Fulfillment or address not found")
    })
    public ResponseEntity<?> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        // Validate fulfillment exists
        FulfillmentDTO fulfillment = dataStore.getFulfillment(request.getFulfillmentId());
        if (fulfillment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Fulfillment not found with id: " + request.getFulfillmentId()));
        }

        // Validate address exists
        AddressDTO address = dataStore.getAddress(request.getAddressId());
        if (address == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Address not found with id: " + request.getAddressId()));
        }

        String shipmentId = "shp-" + UUID.randomUUID().toString().substring(0, 8);
        String trackingNumber = "TRK" + System.currentTimeMillis() +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        ShipmentResponse shipment = ShipmentResponse.builder()
                .shipmentId(shipmentId)
                .fulfillmentId(fulfillment.getFulfillmentId())
                .addressId(address.getAddressId())
                .trackingNumber(trackingNumber)
                .carrier(request.getCarrier())
                .status(ShipmentStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .estimatedDeliveryDate(request.getEstimatedDeliveryDate())
                .build();

        dataStore.saveShipment(shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(shipment));
    }

    @PutMapping("/{shipmentId}")
    @Operation(summary = "Update a shipment", description = "Updates an existing shipment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipment updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<?> updateShipment(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId,
            @Valid @RequestBody UpdateShipmentRequest request) {

        ShipmentResponse existing = dataStore.getShipment(shipmentId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Shipment not found with id: " + shipmentId));
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

        dataStore.saveShipment(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PatchMapping("/{shipmentId}/status")
    @Operation(summary = "Update shipment status", description = "Updates only the status of a shipment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<?> updateShipmentStatus(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId,
            @Valid @RequestBody UpdateShipmentStatusRequest request) {

        ShipmentResponse existing = dataStore.getShipment(shipmentId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Shipment not found with id: " + shipmentId));
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

        dataStore.saveShipment(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{shipmentId}")
    @Operation(summary = "Delete a shipment", description = "Deletes a shipment by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Shipment deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<?> deleteShipment(
            @Parameter(description = "Unique shipment identifier") @PathVariable String shipmentId) {

        ShipmentResponse shipment = dataStore.getShipment(shipmentId);
        if (shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Shipment not found with id: " + shipmentId));
        }

        dataStore.deleteShipment(shipmentId);
        return ResponseEntity.noContent().build();
    }
}

