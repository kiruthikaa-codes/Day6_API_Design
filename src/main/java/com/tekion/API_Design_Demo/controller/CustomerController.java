package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.CustomerDTO;
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

import java.util.*;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {

    // In-memory store for demo purposes
    private final Map<String, CustomerDTO> customers = new HashMap<>();

    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all customers",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class)))
    )
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(new ArrayList<>(customers.values()));
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable String customerId) {
        CustomerDTO customer = customers.get(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Create a new customer", description = "Creates a new customer record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @Parameter(description = "Customer details to create", required = true)
            @RequestBody CustomerDTO customerDTO) {
        String id = customerDTO.getCustomerId() != null ? customerDTO.getCustomerId() : UUID.randomUUID().toString();
        customerDTO.setCustomerId(id);
        customers.put(id, customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerDTO);
    }

    @Operation(summary = "Update a customer", description = "Updates an existing customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "Unique identifier of the customer to update", required = true)
            @PathVariable String customerId,
            @Parameter(description = "Updated customer details", required = true)
            @RequestBody CustomerDTO customerDTO) {
        if (!customers.containsKey(customerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        customerDTO.setCustomerId(customerId);
        customers.put(customerId, customerDTO);
        return ResponseEntity.ok(customerDTO);
    }

    @Operation(summary = "Delete a customer", description = "Deletes a customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Unique identifier of the customer to delete", required = true)
            @PathVariable String customerId) {
        if (!customers.containsKey(customerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        customers.remove(customerId);
        return ResponseEntity.noContent().build();
    }
}

