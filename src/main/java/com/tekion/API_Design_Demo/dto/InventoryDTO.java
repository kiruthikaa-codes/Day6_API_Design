package com.tekion.API_Design_Demo.dto;

import com.tekion.API_Design_Demo.enums.InventoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Inventory.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inventory record details")
public class InventoryDTO {

    @Schema(description = "Unique inventory record ID", example = "inv-123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "Product ID this inventory is for", example = "prod-123e4567-e89b-12d3-a456-426614174000")
    private String productId;

    @Schema(description = "Warehouse/location ID where stock is held", example = "wh-987fcdeb-51a2-3bc4-d567-890123456789")
    private String warehouseId;

    @Schema(description = "Current quantity in stock", example = "150", minimum = "0")
    private Integer quantity;

    @Schema(description = "Quantity reserved for pending orders", example = "25", minimum = "0")
    private Integer reservedQuantity;

    @Schema(description = "Available quantity (quantity - reservedQuantity)", example = "125", minimum = "0")
    private Integer availableQuantity;

    @Schema(description = "Low stock threshold", example = "20", minimum = "0")
    private Integer lowStockThreshold;

    @Schema(description = "Maximum stock capacity", example = "500", minimum = "1")
    private Integer maxCapacity;

    @Schema(description = "Current inventory status", example = "IN_STOCK")
    private InventoryStatus status;

    @Schema(description = "Stock Keeping Unit code", example = "SKU-LAPTOP-001")
    private String sku;

    @Schema(description = "Batch/lot number for tracking", example = "BATCH-2024-001")
    private String batchNumber;

    @Schema(description = "Unit of measurement", example = "pieces")
    private String unit;

    @Schema(description = "Cost per unit", example = "49.99")
    private Double unitCost;

    @Schema(description = "Timestamp when the inventory record was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the inventory record was last updated", example = "2024-01-20T14:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Timestamp of the last restock", example = "2024-01-18T09:00:00")
    private LocalDateTime lastRestockedAt;
}

