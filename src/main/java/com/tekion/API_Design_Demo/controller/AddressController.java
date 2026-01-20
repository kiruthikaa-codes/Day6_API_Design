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
@Tag(
        name = "Address",
        description = "APIs for managing customer postal addresses, including retrieval, creation, update and deletion. " +
                "This demo controller does not persist data, but documents the intended application for the Address service."
)
public class AddressController {

    @Operation(
            summary = "List all addresses",
            description = "Returns the collection of all addresses known to the system. In this demo implementation, " +
                    "an empty list is always returned and no data is stored."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of addresses successfully retrieved (may be empty)."
    )
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(
            summary = "Get a single address",
            description = "Retrieves the address associated with the given address identifier. " +
                    "In a real implementation this would query the Address service or database."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address with the specified identifier was found and returned.",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No address exists for the supplied identifier.",
                    content = @Content)
    })
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(
            @Parameter(
                    description = "Technical identifier of the address resource (for example `addr-001`).",
                    required = true
            )
            @PathVariable String addressId) {
        AddressDTO address = new AddressDTO();
        address.setAddressId(addressId);
        return ResponseEntity.ok(address);
    }

    @Operation(
            summary = "Create a new address",
            description = "Registers a new postal address for a customer. The created representation is returned in the response."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Address was successfully created.",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "The request body is missing required fields or contains invalid values.",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
            @Parameter(
                    description = "Address payload containing customer, street, city, state, postal code and country.",
                    required = true
            )
            @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressDTO);
    }

    @Operation(
            summary = "Update an existing address",
            description = "Replaces the details of an existing address with the values provided in the request body."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address was successfully updated.",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No address exists for the supplied identifier.",
                    content = @Content),
            @ApiResponse(
                    responseCode = "400",
                    description = "The request body is missing required fields or contains invalid values.",
                    content = @Content)
    })
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @Parameter(
                    description = "Identifier of the address that should be updated.",
                    required = true
            )
            @PathVariable String addressId,
            @Parameter(
                    description = "New values for the address fields. Typically mirrors the AddressDTO returned from read operations.",
                    required = true
            )
            @RequestBody AddressDTO addressDTO) {
        addressDTO.setAddressId(addressId);
        return ResponseEntity.ok(addressDTO);
    }

    @Operation(
            summary = "Delete an address",
            description = "Permanently removes the address associated with the given identifier from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Address was successfully deleted. No response body is returned."),
            @ApiResponse(
                    responseCode = "404",
                    description = "No address exists for the supplied identifier.",
                    content = @Content)
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @Parameter(
                    description = "Identifier of the address to delete.",
                    required = true
            )
            @PathVariable String addressId) {
        return ResponseEntity.noContent().build();
    }
}

