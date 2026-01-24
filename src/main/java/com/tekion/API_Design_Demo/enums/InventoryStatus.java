package com.tekion.API_Design_Demo.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the status of an inventory record.
 */
@Schema(description = "Inventory status values")
public enum InventoryStatus {
    
    @Schema(description = "Item is in stock and available")
    IN_STOCK,
    
    @Schema(description = "Item is out of stock")
    OUT_OF_STOCK,
    
    @Schema(description = "Item quantity is below the low stock threshold")
    LOW_STOCK,
    
    @Schema(description = "Item is reserved for pending orders")
    RESERVED,
    
    @Schema(description = "Item is on backorder")
    BACKORDERED,
    
    @Schema(description = "Item has been discontinued")
    DISCONTINUED,
    
    @Schema(description = "Item is in transit to warehouse")
    IN_TRANSIT
}

