package com.tekion.API_Design_Demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tekion.API_Design_Demo.dto.OrderDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Order Management", description = "APIs for managing customer orders")
public class OrderController {

    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all orders",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the order",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Unique identifier of the order", required = true)
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(new OrderDTO());
    }

    @Operation(summary = "Create a new order", description = "Creates a new order in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order successfully created",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data provided", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(
            @Parameter(description = "Order data to create", required = true)
            @RequestBody OrderDTO order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Operation(summary = "Update an existing order", description = "Updates an existing order with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully updated",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data provided", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "Unique identifier of the order to update", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "Updated order data", required = true)
            @RequestBody OrderDTO order) {
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Delete an order", description = "Deletes an order from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/orders/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Unique identifier of the order to delete", required = true)
            @PathVariable UUID orderId) {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get orders by customer", description = "Retrieves all orders placed by a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer orders",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(List.of());
    }
}

