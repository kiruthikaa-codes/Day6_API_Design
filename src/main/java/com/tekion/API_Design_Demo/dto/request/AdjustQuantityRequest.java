package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adjusting inventory quantity.
 * Used for PATCH /api/inventory/{inventoryId}/quantity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for adjusting inventory quantity")
public class AdjustQuantityRequest {
    
    @Schema(
        description = "Quantity adjustment (positive to add, negative to subtract)", 
        example = "-10",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer adjustment;
    
    @Schema(
        description = "Reason for the adjustment", 
        example = "Sold 10 units",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String reason;
    
    @Schema(
        description = "Reference ID (e.g., order ID, return ID)", 
        example = "ord-123e4567-e89b-12d3-a456-426614174000"
    )
    private String referenceId;
    
    @Schema(
        description = "Type of adjustment",
        example = "SALE",
        allowableValues = {"SALE", "RETURN", "DAMAGE", "CORRECTION", "TRANSFER", "OTHER"}
    )
    private String adjustmentType;
}

