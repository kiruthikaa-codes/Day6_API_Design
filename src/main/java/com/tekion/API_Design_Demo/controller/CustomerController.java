package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.AddressDTO;
import com.tekion.API_Design_Demo.dto.CustomerDTO;
import com.tekion.API_Design_Demo.dto.OrderDTO;
import com.tekion.API_Design_Demo.dto.ReviewDTO;
import com.tekion.API_Design_Demo.dto.request.CreateCustomerRequest;
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
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {

    private final DataStore dataStore;

    public CustomerController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers with optional filtering")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all customers"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No customers found")
    })
    @GetMapping
    public ResponseEntity<?> getAllCustomers(
            @Parameter(description = "Filter by name (partial match)") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by email (partial match)") @RequestParam(required = false) String email) {

        List<CustomerDTO> customers = dataStore.getCustomers().values().stream()
                .filter(c -> name == null || (c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(c -> email == null || (c.getEmail() != null && c.getEmail().toLowerCase().contains(email.toLowerCase())))
                .collect(Collectors.toList());

        if (customers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "No customers found"));
        }

        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerById(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable String customerId) {
        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @Operation(summary = "Create a new customer", description = "Creates a new customer record")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        String customerId = "cust-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        CustomerDTO customer = CustomerDTO.builder()
                .customerId(customerId)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .createdAt(now)
                .updatedAt(now)
                .build();

        dataStore.saveCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(customer));
    }

    @Operation(summary = "Update a customer", description = "Updates an existing customer by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(
            @Parameter(description = "Unique identifier of the customer to update", required = true)
            @PathVariable String customerId,
            @Valid @RequestBody CreateCustomerRequest request) {

        CustomerDTO existing = dataStore.getCustomer(customerId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }

        CustomerDTO updated = CustomerDTO.builder()
                .customerId(customerId)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        dataStore.saveCustomer(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "Delete a customer", description = "Deletes a customer by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(
            @Parameter(description = "Unique identifier of the customer to delete", required = true)
            @PathVariable String customerId) {
        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }
        dataStore.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get customer orders", description = "Retrieves all orders for a specific customer")
    @GetMapping("/{customerId}/orders")
    public ResponseEntity<?> getCustomerOrders(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable String customerId) {

        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }

        List<OrderDTO> orders = dataStore.getOrders().values().stream()
                .filter(o -> customerId.equals(o.getCustomerId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @Operation(summary = "Get customer addresses", description = "Retrieves all addresses for a specific customer")
    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<?> getCustomerAddresses(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable String customerId) {

        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }

        List<AddressDTO> addresses = dataStore.getAddresses().values().stream()
                .filter(a -> customerId.equals(a.getCustomerId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @Operation(summary = "Get customer reviews", description = "Retrieves all reviews written by a specific customer")
    @GetMapping("/{customerId}/reviews")
    public ResponseEntity<?> getCustomerReviews(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable String customerId) {

        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }

        List<ReviewDTO> reviews = dataStore.getReviews().values().stream()
                .filter(r -> customerId.equals(r.getCustomerId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
