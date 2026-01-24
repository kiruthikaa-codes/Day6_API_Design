package com.tekion.API_Design_Demo.dto.request;

import com.tekion.API_Design_Demo.enums.InventoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing inventory record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating an inventory record")
public class UpdateInventoryRequest {
    
    @Schema(description = "Warehouse/location ID", example = "wh-987fcdeb-51a2-3bc4-d567-890123456789")
    private String warehouseId;
    
    @Schema(description = "Current quantity", example = "150", minimum = "0")
    private Integer quantity;
    
    @Schema(description = "Reserved quantity", example = "25", minimum = "0")
    private Integer reservedQuantity;
    
    @Schema(description = "Low stock threshold", example = "20", minimum = "0")
    private Integer lowStockThreshold;
    
    @Schema(description = "Maximum stock capacity", example = "500", minimum = "1")
    private Integer maxCapacity;
    
    @Schema(description = "Inventory status", example = "IN_STOCK")
    private InventoryStatus status;
    
    @Schema(description = "Stock Keeping Unit code", example = "SKU-LAPTOP-001")
    private String sku;
    
    @Schema(description = "Batch/lot number", example = "BATCH-2024-001")
    private String batchNumber;
    
    @Schema(description = "Unit of measurement", example = "pieces")
    private String unit;
    
    @Schema(description = "Cost per unit", example = "49.99")
    private Double unitCost;
}

