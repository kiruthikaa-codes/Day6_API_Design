package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.ErrorResponseDTO;
import com.tekion.API_Design_Demo.dto.ProductDTO;
import com.tekion.API_Design_Demo.dto.ReviewDTO;
import com.tekion.API_Design_Demo.dto.InventoryDTO;
import com.tekion.API_Design_Demo.dto.request.CreateProductRequest;
import com.tekion.API_Design_Demo.dto.response.ApiResponse;
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

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "Product management APIs - Version 1")
public class ProductController {

    private final DataStore dataStore;

    public ProductController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Operation(summary = "Get all products", description = "Retrieves a list of all active products with optional filtering")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all products"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts(
            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by minimum price") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Filter by maximum price") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive) {

        List<ProductDTO> products = dataStore.getProducts().values().stream()
                .filter(p -> category == null || category.equalsIgnoreCase(p.getCategory()))
                .filter(p -> minPrice == null || (p.getPrice() != null && p.getPrice() >= minPrice))
                .filter(p -> maxPrice == null || (p.getPrice() != null && p.getPrice() <= maxPrice))
                .filter(p -> isActive == null || isActive.equals(p.getIsActive()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(
            @Parameter(description = "Unique identifier of the product", required = true, example = "prod-101")
            @PathVariable String productId) {
        ProductDTO product = dataStore.getProduct(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + productId));
        }
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "Create a new product", description = "Creates a new product in the catalog")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        String productId = "prod-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        ProductDTO product = ProductDTO.builder()
                .productId(productId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .createdBy("system")
                .build();

        dataStore.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(product));
    }

    @Operation(summary = "Update a product", description = "Updates an existing product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "Unique identifier of the product to update", required = true)
            @PathVariable String productId,
            @Valid @RequestBody CreateProductRequest request) {

        ProductDTO existing = dataStore.getProduct(productId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + productId));
        }

        ProductDTO updated = ProductDTO.builder()
                .productId(productId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : existing.getStockQuantity())
                .isActive(existing.getIsActive())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .updatedAt(LocalDateTime.now())
                .updatedBy("system")
                .build();

        dataStore.saveProduct(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "Delete a product", description = "Soft deletes a product by marking it as inactive")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "Unique identifier of the product to delete", required = true)
            @PathVariable String productId) {
        ProductDTO product = dataStore.getProduct(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + productId));
        }
        dataStore.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search products", description = "Search products by keyword in name or description")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> searchProducts(
            @Parameter(description = "Search keyword", required = true, example = "smartphone")
            @RequestParam String query) {

        String lowerQuery = query.toLowerCase();
        List<ProductDTO> results = dataStore.getProducts().values().stream()
                .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(lowerQuery)) ||
                             (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @Operation(summary = "Get products by category", description = "Retrieves all active products in a specific category")
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(
            @Parameter(description = "Category name", required = true, example = "electronics")
            @PathVariable String category) {

        List<ProductDTO> products = dataStore.getProducts().values().stream()
                .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get product inventory", description = "Retrieves current inventory/stock information for a product")
    @GetMapping("/{productId}/inventory")
    public ResponseEntity<?> getProductInventory(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable String productId) {

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

    @Operation(summary = "Get product reviews", description = "Retrieves all customer reviews for a specific product")
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<?> getProductReviews(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable String productId) {

        ProductDTO product = dataStore.getProduct(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + productId));
        }

        List<ReviewDTO> reviews = dataStore.getReviews().values().stream()
                .filter(r -> productId.equals(r.getProductId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}