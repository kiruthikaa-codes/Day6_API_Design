package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new address. Note: Customer ID is obtained from the X-Customer-Id header (simulating authenticated user context)")
public class CreateAddressRequest {

    @NotBlank(message = "Street is required")
    @Schema(description = "Street address", example = "123 Main Street", requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    @NotBlank(message = "City is required")
    @Schema(description = "City name", example = "San Francisco", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "State is required")
    @Schema(description = "State or province", example = "California", requiredMode = Schema.RequiredMode.REQUIRED)
    private String state;

    @NotBlank(message = "Zip code is required")
    @Schema(description = "ZIP or postal code", example = "94102", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipCode;

    @NotBlank(message = "Country is required")
    @Schema(description = "Country name", example = "USA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;
}

