package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address Data Transfer Object")
public class AddressDTO {

    @Schema(description = "Unique identifier of the address", example = "addr-001")
    private String addressId;

    @Schema(description = "Customer ID this address belongs to (foreign key)", example = "cust-123")
    private String customerId;

    @Schema(description = "Street address", example = "123 Main Street")
    private String street;

    @Schema(description = "City name", example = "San Francisco")
    private String city;

    @Schema(description = "State or province", example = "California")
    private String state;

    @Schema(description = "ZIP or postal code", example = "94102")
    private String zipCode;

    @Schema(description = "Country name", example = "USA")
    private String country;
}

