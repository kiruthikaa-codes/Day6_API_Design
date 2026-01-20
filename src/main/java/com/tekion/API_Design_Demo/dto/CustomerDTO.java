package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer Data Transfer Object")
public class CustomerDTO {

    @Schema(description = "Unique identifier of the customer", example = "cust-001")
    private String customerId;

    @Schema(description = "Full name of the customer", example = "John Doe")
    private String name;

    @Schema(description = "Email address of the customer", example = "john.doe@example.com", format = "email")
    private String email;

    @Schema(description = "Phone number of the customer", example = "+1-555-123-4567")
    private String phone;
}

