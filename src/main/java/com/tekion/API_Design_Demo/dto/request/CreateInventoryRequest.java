package com.tekion.API_Design_Demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new inventory record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new inventory record")
public class CreateInventoryRequest {
    
    @Schema(description = "Product ID this inventory is for", example = "prod-123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productId;
    
    @Schema(description = "Warehouse/location ID where stock is held", example = "wh-987fcdeb-51a2-3bc4-d567-890123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String warehouseId;
    
    @Schema(description = "Initial quantity", example = "100", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
    
    @Schema(description = "Low stock threshold", example = "20", minimum = "0")
    private Integer lowStockThreshold;
    
    @Schema(description = "Maximum stock capacity", example = "500", minimum = "1")
    private Integer maxCapacity;
    
    @Schema(description = "Stock Keeping Unit code", example = "SKU-LAPTOP-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sku;
    
    @Schema(description = "Batch/lot number for tracking", example = "BATCH-2024-001")
    private String batchNumber;
    
    @Schema(description = "Unit of measurement", example = "pieces")
    private String unit;
    
    @Schema(description = "Cost per unit", example = "49.99")
    private Double unitCost;
}

