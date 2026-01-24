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
@Schema(description = "Customer Data Transfer Object")
public class CustomerDTO {

    @Schema(description = "Unique identifier of the customer (auto-generated)", example = "cust-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerId;

    @Schema(description = "Full name of the customer", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Email address of the customer", example = "john.doe@example.com", format = "email", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Phone number of the customer", example = "+1-555-123-4567")
    private String phone;

    @Schema(description = "Timestamp when customer was created", example = "2025-01-20T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private java.time.LocalDateTime createdAt;

    @Schema(description = "Timestamp when customer was last updated", example = "2025-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private java.time.LocalDateTime updatedAt;
}

