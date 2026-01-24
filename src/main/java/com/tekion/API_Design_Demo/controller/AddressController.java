package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.AddressDTO;
import com.tekion.API_Design_Demo.dto.CustomerDTO;
import com.tekion.API_Design_Demo.dto.request.CreateAddressRequest;
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
@RequestMapping("/api/v1/addresses")
@Tag(name = "Address", description = "APIs for managing customer postal addresses")
public class AddressController {

    private final DataStore dataStore;

    public AddressController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Operation(summary = "List all addresses", description = "Returns the collection of all addresses with optional filtering")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of addresses successfully retrieved")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAllAddresses(
            @Parameter(description = "Filter by customer ID") @RequestParam(required = false) String customerId,
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by state") @RequestParam(required = false) String state) {

        List<AddressDTO> addresses = dataStore.getAddresses().values().stream()
                .filter(a -> customerId == null || customerId.equals(a.getCustomerId()))
                .filter(a -> city == null || city.equalsIgnoreCase(a.getCity()))
                .filter(a -> state == null || state.equalsIgnoreCase(a.getState()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @Operation(summary = "Get a single address", description = "Retrieves the address by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    })
    @GetMapping("/{addressId}")
    public ResponseEntity<?> getAddressById(
            @Parameter(description = "Address ID", required = true)
            @PathVariable String addressId) {
        AddressDTO address = dataStore.getAddress(addressId);
        if (address == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Address not found with id: " + addressId));
        }
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @Operation(summary = "Create a new address",
            description = "Registers a new postal address for the authenticated customer. Customer ID is obtained from the X-Customer-Id header (simulating authentication context).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or missing X-Customer-Id header"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PostMapping
    public ResponseEntity<?> createAddress(
            @Parameter(description = "Customer ID (simulates authenticated user context)", required = true, example = "cust-12345678")
            @RequestHeader("X-Customer-Id") String customerId,
            @Valid @RequestBody CreateAddressRequest request) {
        // Validate customer exists (customerId comes from auth header, not request body)
        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }

        String addressId = "addr-" + UUID.randomUUID().toString().substring(0, 8);

        AddressDTO address = AddressDTO.builder()
                .addressId(addressId)
                .customerId(customer.getCustomerId())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .createdAt(LocalDateTime.now())
                .build();

        dataStore.saveAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(address));
    }

    @Operation(summary = "Update an existing address", description = "Updates address details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(
            @Parameter(description = "Address ID", required = true)
            @PathVariable String addressId,
            @Valid @RequestBody CreateAddressRequest request) {

        AddressDTO existing = dataStore.getAddress(addressId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Address not found with id: " + addressId));
        }

        AddressDTO updated = AddressDTO.builder()
                .addressId(addressId)
                .customerId(existing.getCustomerId())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .createdAt(existing.getCreatedAt())
                .build();

        dataStore.saveAddress(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "Delete an address", description = "Permanently removes the address")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Address deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @Parameter(description = "Address ID", required = true)
            @PathVariable String addressId) {
        AddressDTO address = dataStore.getAddress(addressId);
        if (address == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Address not found with id: " + addressId));
        }
        dataStore.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

}
