package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.CustomerDTO;
import com.tekion.API_Design_Demo.dto.OrderDTO;
import com.tekion.API_Design_Demo.dto.OrderItemDTO;
import com.tekion.API_Design_Demo.dto.ProductDTO;
import com.tekion.API_Design_Demo.dto.request.CreateOrderRequest;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Order Management", description = "APIs for managing customer orders")
public class OrderController {

    private final DataStore dataStore;

    public OrderController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders with optional filtering by customer, status, and date range")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all orders")
    })
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(
            @Parameter(description = "Filter by customer ID") @RequestParam(required = false) String customerId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by start date (orders on or after this date)") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Filter by end date (orders on or before this date)") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Filter by minimum order amount") @RequestParam(required = false) BigDecimal minAmount,
            @Parameter(description = "Filter by maximum order amount") @RequestParam(required = false) BigDecimal maxAmount) {

        List<OrderDTO> orders = dataStore.getOrders().values().stream()
                .filter(o -> customerId == null || customerId.equals(o.getCustomerId()))
                .filter(o -> status == null || status.equalsIgnoreCase(o.getStatus()))
                .filter(o -> startDate == null || (o.getOrderDate() != null && !o.getOrderDate().toLocalDate().isBefore(startDate)))
                .filter(o -> endDate == null || (o.getOrderDate() != null && !o.getOrderDate().toLocalDate().isAfter(endDate)))
                .filter(o -> minAmount == null || (o.getTotalAmount() != null && o.getTotalAmount().compareTo(minAmount) >= 0))
                .filter(o -> maxAmount == null || (o.getTotalAmount() != null && o.getTotalAmount().compareTo(maxAmount) <= 0))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the order"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(
            @Parameter(description = "Unique identifier of the order", required = true)
            @PathVariable String orderId) {
        OrderDTO order = dataStore.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Order not found with id: " + orderId));
        }
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(summary = "Create a new order",
            description = "Creates a new order for the authenticated customer. Customer ID is obtained from the X-Customer-Id header (simulating authentication context). Price is calculated from product prices.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order successfully created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid order data or missing X-Customer-Id header"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer or product not found")
    })
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(
            @Parameter(description = "Customer ID (simulates authenticated user context)", required = true, example = "cust-12345678")
            @RequestHeader("X-Customer-Id") String customerId,
            @Valid @RequestBody CreateOrderRequest request) {
        // Validate customer exists (customerId comes from auth header, not request body)
        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }

        // Build order items and calculate total from product prices
        List<OrderItemDTO> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            ProductDTO product = dataStore.getProduct(itemRequest.getProductId());
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + itemRequest.getProductId()));
            }

            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItemDTO orderItem = OrderItemDTO.builder()
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(itemTotal);
        }

        String orderId = "ord-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        OrderDTO order = OrderDTO.builder()
                .orderId(orderId)
                .customerId(customer.getCustomerId())
                .customerName(customer.getName())
                .items(orderItems)
                .orderDate(now)
                .status("PENDING")
                .totalAmount(totalAmount)
                .shippingAddressId(request.getShippingAddressId())
                .createdAt(now)
                .updatedAt(now)
                .build();

        dataStore.saveOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(order));
    }

    @Operation(summary = "Update order status", description = "Updates the status of an existing order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @Parameter(description = "Unique identifier of the order", required = true)
            @PathVariable String orderId,
            @Parameter(description = "New status", required = true)
            @RequestParam String status) {

        OrderDTO existing = dataStore.getOrder(orderId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Order not found with id: " + orderId));
        }

        OrderDTO updated = OrderDTO.builder()
                .orderId(existing.getOrderId())
                .customerId(existing.getCustomerId())
                .customerName(existing.getCustomerName())
                .items(existing.getItems())
                .orderDate(existing.getOrderDate())
                .status(status)
                .totalAmount(existing.getTotalAmount())
                .shippingAddressId(existing.getShippingAddressId())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        dataStore.saveOrder(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "Delete an order", description = "Deletes an order from the system")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Order successfully deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> deleteOrder(
            @Parameter(description = "Unique identifier of the order to delete", required = true)
            @PathVariable String orderId) {
        OrderDTO order = dataStore.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Order not found with id: " + orderId));
        }
        dataStore.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
