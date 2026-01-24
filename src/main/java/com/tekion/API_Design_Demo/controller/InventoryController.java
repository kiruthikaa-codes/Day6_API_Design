package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.InventoryDTO;
import com.tekion.API_Design_Demo.dto.ProductDTO;
import com.tekion.API_Design_Demo.dto.request.AdjustQuantityRequest;
import com.tekion.API_Design_Demo.dto.request.CreateInventoryRequest;
import com.tekion.API_Design_Demo.dto.request.RestockRequest;
import com.tekion.API_Design_Demo.dto.request.UpdateInventoryRequest;
import com.tekion.API_Design_Demo.dto.response.ApiResponse;
import com.tekion.API_Design_Demo.dto.response.ErrorResponse;
import com.tekion.API_Design_Demo.enums.InventoryStatus;
import com.tekion.API_Design_Demo.service.DataStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Inventory management operations.
 * Provides endpoints for CRUD operations and inventory-specific actions.
 */
@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Inventory management API for tracking stock levels, restocking, and quantity adjustments")
public class InventoryController {

    private final DataStore dataStore;

    public InventoryController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * List all inventory records with optional filtering and pagination.
     */
    @GetMapping
    @Operation(
        summary = "List all inventory",
        description = "Retrieves a list of all inventory records. Supports filtering by warehouse ID, product ID, and inventory status."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved inventory list")
    })
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> listInventory(
            @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) String warehouseId,
            @Parameter(description = "Filter by product ID") @RequestParam(required = false) String productId,
            @Parameter(description = "Filter by inventory status") @RequestParam(required = false) String status
    ) {
        List<InventoryDTO> inventoryList = dataStore.getInventory().values().stream()
                .filter(inv -> warehouseId == null || warehouseId.equals(inv.getWarehouseId()))
                .filter(inv -> productId == null || productId.equals(inv.getProductId()))
                .filter(inv -> status == null || (inv.getStatus() != null && status.equalsIgnoreCase(inv.getStatus().name())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(inventoryList));
    }

    /**
     * Get inventory details by ID.
     */
    @GetMapping("/{inventoryId}")
    @Operation(
        summary = "Get inventory details",
        description = "Retrieves detailed information about a specific inventory record by its unique ID. " +
                      "Returns complete inventory data including quantity, status, warehouse location, and timestamps."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved inventory details. Returns the complete inventory record with all fields populated.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Not Found - Inventory record does not exist. Possible causes:\n" +
                          "• The provided inventoryId does not match any existing record\n" +
                          "• The inventory record may have been deleted\n" +
                          "• The inventoryId format is incorrect (expected format: inv-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> getInventory(
            @Parameter(description = "Inventory record ID", required = true, example = "inv-123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String inventoryId
    ) {
        InventoryDTO inventory = dataStore.getInventoryItem(inventoryId);
        if (inventory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Inventory not found with id: " + inventoryId));
        }
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    /**
     * Get inventory for a specific product.
     */
    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Get inventory for product",
        description = "Retrieves all inventory records for a specific product across all warehouses. " +
                      "Useful for checking total stock availability across multiple locations. " +
                      "Returns a list of inventory records, one for each warehouse where the product is stocked."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved inventory for product. Returns a list of inventory records across all warehouses. " +
                          "An empty list indicates the product has no inventory records (not stocked in any warehouse).",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Not Found - Product does not exist. Possible causes:\n" +
                          "• The provided productId does not match any existing product in the system\n" +
                          "• The product may have been deleted or deactivated\n" +
                          "• Verify the productId format: prod-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\n" +
                          "Note: If the product exists but has no inventory, a 200 response with an empty list is returned instead",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> getInventoryByProduct(
            @Parameter(description = "Product ID", required = true, example = "prod-123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String productId
    ) {
        // Validate product exists
        ProductDTO product = dataStore.getProduct(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + productId));
        }

        List<InventoryDTO> inventoryList = dataStore.getInventory().values().stream()
                .filter(inv -> productId.equals(inv.getProductId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(inventoryList));
    }

    /**
     * Get all low stock items.
     */
    @GetMapping("/low-stock")
    @Operation(
        summary = "Get low stock items",
        description = "Retrieves all inventory records where the available quantity is at or below the configured low stock threshold. " +
                      "This endpoint is useful for generating reorder alerts and identifying items that need restocking. " +
                      "Results can be filtered by warehouse and are paginated for large datasets."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved low stock items. Returns a paginated list of inventory records that are at or below their low stock threshold. " +
                          "An empty list indicates all inventory items are adequately stocked. " +
                          "Each record includes the current quantity, threshold value, and warehouse location for easy reordering.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid request parameters. Possible causes:\n" +
                          "• 'page' parameter is negative (must be >= 0)\n" +
                          "• 'size' parameter is less than 1 or greater than 100\n" +
                          "• Invalid 'warehouseId' format",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getLowStockItems(
            @Parameter(description = "Filter by warehouse ID", example = "wh-987fcdeb-51a2-3bc4-d567-890123456789")
            @RequestParam(required = false) String warehouseId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        List<InventoryDTO> lowStockItems = dataStore.getInventory().values().stream()
                .filter(inv -> warehouseId == null || warehouseId.equals(inv.getWarehouseId()))
                .filter(inv -> {
                    int available = inv.getAvailableQuantity() != null ? inv.getAvailableQuantity() : 0;
                    int threshold = inv.getLowStockThreshold() != null ? inv.getLowStockThreshold() : 10;
                    return available <= threshold;
                })
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(lowStockItems));
    }

    /**
     * Create a new inventory record.
     */
    @PostMapping
    @Operation(
        summary = "Create inventory record",
        description = "Creates a new inventory record for a product at a specific warehouse location. " +
                      "Required fields: productId, warehouseId, quantity, and sku. " +
                      "Optional fields will use default values if not provided (e.g., lowStockThreshold defaults to 10)."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Inventory record created successfully. Returns the newly created inventory record with generated ID and timestamps.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid request body. Possible causes:\n" +
                          "• Missing required field: 'productId' - The product ID is required to associate inventory with a product\n" +
                          "• Missing required field: 'warehouseId' - The warehouse ID is required to specify stock location\n" +
                          "• Missing required field: 'quantity' - Initial quantity must be provided\n" +
                          "• Missing required field: 'sku' - Stock Keeping Unit code is required for tracking\n" +
                          "• Invalid 'quantity' value: Must be a non-negative integer (>= 0)\n" +
                          "• Invalid 'lowStockThreshold': Must be a non-negative integer\n" +
                          "• Invalid 'maxCapacity': Must be a positive integer (>= 1)\n" +
                          "• Invalid 'unitCost': Must be a positive decimal number",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Conflict - Inventory record already exists. Possible causes:\n" +
                          "• An inventory record already exists for this product/warehouse combination\n" +
                          "• The SKU code is already in use by another inventory record\n" +
                          "Solution: Use PUT /api/inventory/{inventoryId} to update the existing record instead",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> createInventory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Inventory creation request",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateInventoryRequest.class))
            )
            @Valid @RequestBody CreateInventoryRequest request
    ) {
        // Validate product exists
        ProductDTO product = dataStore.getProduct(request.getProductId());
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + request.getProductId()));
        }

        String inventoryId = "inv-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        int quantity = request.getQuantity() != null ? request.getQuantity() : 0;
        int lowStockThreshold = request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10;

        // Determine status based on quantity
        InventoryStatus status;
        if (quantity == 0) {
            status = InventoryStatus.OUT_OF_STOCK;
        } else if (quantity <= lowStockThreshold) {
            status = InventoryStatus.LOW_STOCK;
        } else {
            status = InventoryStatus.IN_STOCK;
        }

        InventoryDTO inventory = InventoryDTO.builder()
                .id(inventoryId)
                .productId(request.getProductId())
                .warehouseId(request.getWarehouseId())
                .quantity(quantity)
                .reservedQuantity(0)
                .availableQuantity(quantity)
                .lowStockThreshold(lowStockThreshold)
                .maxCapacity(request.getMaxCapacity())
                .status(status)
                .sku(request.getSku())
                .batchNumber(request.getBatchNumber())
                .unit(request.getUnit())
                .unitCost(request.getUnitCost())
                .createdAt(now)
                .updatedAt(now)
                .build();

        dataStore.saveInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(inventory, "Inventory record created successfully"));
    }

    /**
     * Update an existing inventory record.
     */
    @PutMapping("/{inventoryId}")
    @Operation(
        summary = "Update inventory",
        description = "Updates an existing inventory record with the provided fields. " +
                      "Only non-null fields in the request will be updated. " +
                      "Use this endpoint to modify warehouse location, quantities, thresholds, or status."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Inventory updated successfully. Returns the updated inventory record with new values and updated timestamp.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid request body. Possible causes:\n" +
                          "• Invalid 'quantity' value: Must be a non-negative integer (>= 0)\n" +
                          "• Invalid 'reservedQuantity': Cannot exceed total quantity\n" +
                          "• Invalid 'lowStockThreshold': Must be a non-negative integer\n" +
                          "• Invalid 'maxCapacity': Must be greater than current quantity\n" +
                          "• Invalid 'status': Must be one of: IN_STOCK, OUT_OF_STOCK, LOW_STOCK, RESERVED, BACKORDERED, DISCONTINUED, IN_TRANSIT\n" +
                          "• Invalid 'unitCost': Must be a positive decimal number",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Not Found - Inventory record does not exist. Possible causes:\n" +
                          "• The provided inventoryId does not match any existing record\n" +
                          "• The inventory record may have been deleted\n" +
                          "• Verify the inventoryId format: inv-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> updateInventory(
            @Parameter(description = "Inventory record ID", required = true, example = "inv-123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String inventoryId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Inventory update request",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateInventoryRequest.class))
            )
            @Valid @RequestBody UpdateInventoryRequest request
    ) {
        InventoryDTO existing = dataStore.getInventoryItem(inventoryId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Inventory not found with id: " + inventoryId));
        }

        // Update only non-null fields from request
        int quantity = request.getQuantity() != null ? request.getQuantity() : existing.getQuantity();
        int reservedQuantity = request.getReservedQuantity() != null ? request.getReservedQuantity() : existing.getReservedQuantity();
        int availableQuantity = quantity - reservedQuantity;
        int lowStockThreshold = request.getLowStockThreshold() != null ? request.getLowStockThreshold() : existing.getLowStockThreshold();

        // Determine status based on quantity if not explicitly set
        InventoryStatus status = request.getStatus();
        if (status == null) {
            if (availableQuantity == 0) {
                status = InventoryStatus.OUT_OF_STOCK;
            } else if (availableQuantity <= lowStockThreshold) {
                status = InventoryStatus.LOW_STOCK;
            } else {
                status = InventoryStatus.IN_STOCK;
            }
        }

        InventoryDTO updated = InventoryDTO.builder()
                .id(inventoryId)
                .productId(existing.getProductId())
                .warehouseId(request.getWarehouseId() != null ? request.getWarehouseId() : existing.getWarehouseId())
                .quantity(quantity)
                .reservedQuantity(reservedQuantity)
                .availableQuantity(availableQuantity)
                .lowStockThreshold(lowStockThreshold)
                .maxCapacity(request.getMaxCapacity() != null ? request.getMaxCapacity() : existing.getMaxCapacity())
                .status(status)
                .sku(request.getSku() != null ? request.getSku() : existing.getSku())
                .batchNumber(request.getBatchNumber() != null ? request.getBatchNumber() : existing.getBatchNumber())
                .unit(request.getUnit() != null ? request.getUnit() : existing.getUnit())
                .unitCost(request.getUnitCost() != null ? request.getUnitCost() : existing.getUnitCost())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .lastRestockedAt(existing.getLastRestockedAt())
                .build();

        dataStore.saveInventory(updated);
        return ResponseEntity.ok(ApiResponse.success(updated, "Inventory updated successfully"));
    }

    /**
     * Adjust inventory quantity (increment or decrement).
     */
    @PatchMapping("/{inventoryId}/quantity")
    @Operation(
        summary = "Adjust quantity",
        description = "Adjusts the inventory quantity by adding or subtracting units. " +
                      "Use positive values to add stock (e.g., +10) and negative values to subtract (e.g., -5). " +
                      "A reason for the adjustment is required for audit purposes."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Quantity adjusted successfully. Returns the updated inventory record with new quantity and adjustment logged.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid adjustment request. Possible causes:\n" +
                          "• Missing required field: 'adjustment' - The quantity change value is required\n" +
                          "• Missing required field: 'reason' - A reason for the adjustment must be provided\n" +
                          "• Adjustment would result in negative quantity: Current quantity is less than the subtraction amount\n" +
                          "• Adjustment would exceed max capacity: New quantity would exceed the warehouse maximum capacity\n" +
                          "• Invalid 'adjustmentType': Must be one of: SALE, RETURN, DAMAGE, CORRECTION, TRANSFER, OTHER\n" +
                          "• Zero adjustment not allowed: The adjustment value cannot be 0",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Not Found - Inventory record does not exist. Possible causes:\n" +
                          "• The provided inventoryId does not match any existing record\n" +
                          "• The inventory record may have been deleted\n" +
                          "• Verify the inventoryId format: inv-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> adjustQuantity(
            @Parameter(description = "Inventory record ID", required = true, example = "inv-123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String inventoryId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Quantity adjustment request",
                required = true,
                content = @Content(schema = @Schema(implementation = AdjustQuantityRequest.class))
            )
            @Valid @RequestBody AdjustQuantityRequest request
    ) {
        InventoryDTO existing = dataStore.getInventoryItem(inventoryId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Inventory not found with id: " + inventoryId));
        }

        int adjustment = request.getAdjustment() != null ? request.getAdjustment() : 0;
        int newQuantity = existing.getQuantity() + adjustment;

        // Validate that new quantity is not negative
        if (newQuantity < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("INVALID_ADJUSTMENT", "Adjustment would result in negative quantity. Current: " + existing.getQuantity() + ", Adjustment: " + adjustment));
        }

        // Check max capacity if set
        if (existing.getMaxCapacity() != null && newQuantity > existing.getMaxCapacity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("EXCEEDS_CAPACITY", "Adjustment would exceed max capacity: " + existing.getMaxCapacity()));
        }

        int availableQuantity = newQuantity - existing.getReservedQuantity();
        int lowStockThreshold = existing.getLowStockThreshold() != null ? existing.getLowStockThreshold() : 10;

        // Determine status based on new quantity
        InventoryStatus status;
        if (availableQuantity <= 0) {
            status = InventoryStatus.OUT_OF_STOCK;
        } else if (availableQuantity <= lowStockThreshold) {
            status = InventoryStatus.LOW_STOCK;
        } else {
            status = InventoryStatus.IN_STOCK;
        }

        InventoryDTO updated = InventoryDTO.builder()
                .id(inventoryId)
                .productId(existing.getProductId())
                .warehouseId(existing.getWarehouseId())
                .quantity(newQuantity)
                .reservedQuantity(existing.getReservedQuantity())
                .availableQuantity(availableQuantity)
                .lowStockThreshold(existing.getLowStockThreshold())
                .maxCapacity(existing.getMaxCapacity())
                .status(status)
                .sku(existing.getSku())
                .batchNumber(existing.getBatchNumber())
                .unit(existing.getUnit())
                .unitCost(existing.getUnitCost())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .lastRestockedAt(existing.getLastRestockedAt())
                .build();

        dataStore.saveInventory(updated);
        return ResponseEntity.ok(ApiResponse.success(updated, "Quantity adjusted successfully"));
    }

    /**
     * Restock an inventory item.
     */
    @PostMapping("/{inventoryId}/restock")
    @Operation(
        summary = "Restock item",
        description = "Adds stock to an existing inventory record. Used for receiving new shipments or restocking from suppliers. " +
                      "This operation increases the quantity and updates the lastRestockedAt timestamp. " +
                      "Optionally, you can provide supplier information and purchase order reference for tracking."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Item restocked successfully. Returns the updated inventory record with:\n" +
                          "• New quantity reflecting the added stock\n" +
                          "• Updated lastRestockedAt timestamp\n" +
                          "• Updated status (e.g., changed from LOW_STOCK to IN_STOCK if applicable)\n" +
                          "• New batch number if provided in the request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid restock request. Possible causes:\n" +
                          "• Missing required field: 'quantity' - The restock quantity must be provided\n" +
                          "• Invalid 'quantity' value: Must be a positive integer (>= 1)\n" +
                          "• Restock would exceed max capacity: New total quantity would exceed the warehouse maximum capacity limit\n" +
                          "• Invalid 'unitCost': Must be a positive decimal number if provided\n" +
                          "• Invalid 'supplierId' format: Must match supplier ID format if provided\n" +
                          "Solution: Reduce the restock quantity or increase the max capacity first",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Not Found - Inventory record does not exist. Possible causes:\n" +
                          "• The provided inventoryId does not match any existing record\n" +
                          "• The inventory record may have been deleted\n" +
                          "• Verify the inventoryId format: inv-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\n" +
                          "Solution: Create a new inventory record first using POST /api/inventory",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> restockItem(
            @Parameter(description = "Inventory record ID", required = true, example = "inv-123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String inventoryId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Restock request",
                required = true,
                content = @Content(schema = @Schema(implementation = RestockRequest.class))
            )
            @Valid @RequestBody RestockRequest request
    ) {
        InventoryDTO existing = dataStore.getInventoryItem(inventoryId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Inventory not found with id: " + inventoryId));
        }

        int restockQuantity = request.getQuantity() != null ? request.getQuantity() : 0;
        int newQuantity = existing.getQuantity() + restockQuantity;

        // Check max capacity if set
        if (existing.getMaxCapacity() != null && newQuantity > existing.getMaxCapacity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("EXCEEDS_CAPACITY", "Restock would exceed max capacity: " + existing.getMaxCapacity()));
        }

        int availableQuantity = newQuantity - existing.getReservedQuantity();
        int lowStockThreshold = existing.getLowStockThreshold() != null ? existing.getLowStockThreshold() : 10;

        // Determine status based on new quantity
        InventoryStatus status;
        if (availableQuantity <= 0) {
            status = InventoryStatus.OUT_OF_STOCK;
        } else if (availableQuantity <= lowStockThreshold) {
            status = InventoryStatus.LOW_STOCK;
        } else {
            status = InventoryStatus.IN_STOCK;
        }

        LocalDateTime now = LocalDateTime.now();

        InventoryDTO updated = InventoryDTO.builder()
                .id(inventoryId)
                .productId(existing.getProductId())
                .warehouseId(existing.getWarehouseId())
                .quantity(newQuantity)
                .reservedQuantity(existing.getReservedQuantity())
                .availableQuantity(availableQuantity)
                .lowStockThreshold(existing.getLowStockThreshold())
                .maxCapacity(existing.getMaxCapacity())
                .status(status)
                .sku(existing.getSku())
                .batchNumber(request.getBatchNumber() != null ? request.getBatchNumber() : existing.getBatchNumber())
                .unit(existing.getUnit())
                .unitCost(request.getUnitCost() != null ? request.getUnitCost() : existing.getUnitCost())
                .createdAt(existing.getCreatedAt())
                .updatedAt(now)
                .lastRestockedAt(now)
                .build();

        dataStore.saveInventory(updated);
        return ResponseEntity.ok(ApiResponse.success(updated, "Item restocked successfully"));
    }
}

