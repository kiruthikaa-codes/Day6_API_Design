package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.AddressDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Address", description = "Address management APIs")
public class AddressController {

    @Operation(summary = "Get all addresses", description = "Retrieves a list of all addresses")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all addresses")
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(summary = "Get address by ID", description = "Retrieves an address by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address found",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(
            @Parameter(description = "Unique identifier of the address", required = true)
            @PathVariable String addressId) {
        AddressDTO address = new AddressDTO();
        address.setAddressId(addressId);
        return ResponseEntity.ok(address);
    }

    @Operation(summary = "Create a new address", description = "Creates a new address for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
            @Parameter(description = "Address details to create", required = true)
            @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressDTO);
    }

    @Operation(summary = "Update an address", description = "Updates an existing address by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @Parameter(description = "Unique identifier of the address to update", required = true)
            @PathVariable String addressId,
            @Parameter(description = "Updated address details", required = true)
            @RequestBody AddressDTO addressDTO) {
        addressDTO.setAddressId(addressId);
        return ResponseEntity.ok(addressDTO);
    }

    @Operation(summary = "Delete an address", description = "Deletes an address by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "Unique identifier of the address to delete", required = true)
            @PathVariable String addressId) {
        return ResponseEntity.noContent().build();
    }
}

