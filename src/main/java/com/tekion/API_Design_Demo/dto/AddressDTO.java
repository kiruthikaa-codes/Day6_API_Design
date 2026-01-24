package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address Data Transfer Object")
public class AddressDTO {

    @Schema(description = "Unique identifier of the address (auto-generated)", example = "addr-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String addressId;

    @Schema(description = "Customer ID this address belongs to", example = "cust-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerId;

    @Schema(description = "Street address", example = "123 Main Street", requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    @Schema(description = "City name", example = "San Francisco", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @Schema(description = "State or province", example = "California", requiredMode = Schema.RequiredMode.REQUIRED)
    private String state;

    @Schema(description = "ZIP or postal code", example = "94102", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipCode;

    @Schema(description = "Country name", example = "USA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;

    @Schema(description = "Timestamp when address was created", example = "2025-01-20T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private java.time.LocalDateTime createdAt;
}

