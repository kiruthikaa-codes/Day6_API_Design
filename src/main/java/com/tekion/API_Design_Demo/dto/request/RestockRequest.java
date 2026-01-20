package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for restocking inventory.
 * Used for POST /api/inventory/{inventoryId}/restock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for restocking inventory")
public class RestockRequest {
    
    @Schema(
        description = "Quantity to add to stock", 
        example = "100",
        minimum = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer quantity;
    
    @Schema(
        description = "New batch/lot number for the restocked items", 
        example = "BATCH-2024-002"
    )
    private String batchNumber;
    
    @Schema(
        description = "Cost per unit for this restock", 
        example = "45.99"
    )
    private Double unitCost;
    
    @Schema(
        description = "Supplier/vendor ID", 
        example = "sup-123e4567-e89b-12d3-a456-426614174000"
    )
    private String supplierId;
    
    @Schema(
        description = "Purchase order reference", 
        example = "PO-2024-00123"
    )
    private String purchaseOrderId;
    
    @Schema(
        description = "Notes about the restock", 
        example = "Regular monthly restock from primary supplier"
    )
    private String notes;
}

