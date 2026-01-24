package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.FulfillmentDTO;
import com.tekion.API_Design_Demo.dto.FulfillmentDTO.FulfillmentStatus;
import com.tekion.API_Design_Demo.dto.OrderDTO;
import com.tekion.API_Design_Demo.dto.request.CreateFulfillmentRequest;
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
@RequestMapping("/api/v1/fulfillments")
@Tag(name = "Fulfillment", description = "Fulfillment management APIs")
public class FulfillmentController {

    private final DataStore dataStore;

    public FulfillmentController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Operation(
            summary = "Get all fulfillments",
            description = "Retrieves all fulfillments with optional filtering by orderId and status."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved fulfillments")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<FulfillmentDTO>>> getFulfillments(
            @Parameter(description = "Filter by order ID") @RequestParam(required = false) String orderId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) FulfillmentStatus status) {

        List<FulfillmentDTO> fulfillments = dataStore.getFulfillments().values().stream()
                .filter(f -> orderId == null || orderId.equals(f.getOrderId()))
                .filter(f -> status == null || status.equals(f.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(fulfillments));
    }

    @Operation(
            summary = "Get fulfillment by ID",
            description = "Retrieves a single fulfillment by its unique identifier"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fulfillment found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Fulfillment not found")
    })
    @GetMapping("/{fulfillmentId}")
    public ResponseEntity<?> getFulfillmentById(
            @Parameter(description = "Fulfillment ID", required = true)
            @PathVariable String fulfillmentId) {

        FulfillmentDTO fulfillment = dataStore.getFulfillment(fulfillmentId);
        if (fulfillment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Fulfillment not found with id: " + fulfillmentId));
        }
        return ResponseEntity.ok(ApiResponse.success(fulfillment));
    }

    @Operation(
            summary = "Create a new fulfillment",
            description = "Creates a new fulfillment for an order. Server generates fulfillmentId and timestamps."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Fulfillment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping
    public ResponseEntity<?> createFulfillment(@Valid @RequestBody CreateFulfillmentRequest request) {
        // Validate order exists
        OrderDTO order = dataStore.getOrder(request.getOrderId());
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Order not found with id: " + request.getOrderId()));
        }

        String fulfillmentId = "ful-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        FulfillmentDTO fulfillment = new FulfillmentDTO();
        fulfillment.setFulfillmentId(fulfillmentId);
        fulfillment.setOrderId(order.getOrderId());
        fulfillment.setStatus(FulfillmentStatus.PENDING);
        fulfillment.setCreatedAt(now);
        fulfillment.setUpdatedAt(now);

        dataStore.saveFulfillment(fulfillment);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(fulfillment));
    }

    @Operation(
            summary = "Update fulfillment status",
            description = "Updates the status of a fulfillment"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fulfillment updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Fulfillment not found")
    })
    @PatchMapping("/{fulfillmentId}/status")
    public ResponseEntity<?> updateFulfillmentStatus(
            @Parameter(description = "Fulfillment ID", required = true) @PathVariable String fulfillmentId,
            @Parameter(description = "New status", required = true) @RequestParam FulfillmentStatus status) {

        FulfillmentDTO existing = dataStore.getFulfillment(fulfillmentId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Fulfillment not found with id: " + fulfillmentId));
        }

        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        dataStore.saveFulfillment(existing);

        return ResponseEntity.ok(ApiResponse.success(existing));
    }

    @Operation(
            summary = "Get fulfillments for order",
            description = "Retrieves all fulfillments for a specific order"
    )
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getFulfillmentsByOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable String orderId) {

        OrderDTO order = dataStore.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Order not found with id: " + orderId));
        }

        List<FulfillmentDTO> fulfillments = dataStore.getFulfillments().values().stream()
                .filter(f -> orderId.equals(f.getOrderId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(fulfillments));
    }

    @Operation(
            summary = "Delete a fulfillment",
            description = "Deletes a fulfillment by its ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Fulfillment deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Fulfillment not found")
    })
    @DeleteMapping("/{fulfillmentId}")
    public ResponseEntity<?> deleteFulfillment(
            @Parameter(description = "Fulfillment ID", required = true) @PathVariable String fulfillmentId) {

        FulfillmentDTO fulfillment = dataStore.getFulfillment(fulfillmentId);
        if (fulfillment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Fulfillment not found with id: " + fulfillmentId));
        }

        dataStore.deleteFulfillment(fulfillmentId);
        return ResponseEntity.noContent().build();
    }
}