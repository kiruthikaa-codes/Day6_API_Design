package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.ErrorResponseDTO;
import com.tekion.API_Design_Demo.dto.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "Product management APIs - Version 1")
public class ProductController {

    @Operation(summary = "Get all products", description = "Retrieves a list of all active products in the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all products"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found successfully",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found - The product with the specified ID does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product ID format - Product ID must be alphanumeric",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Unique identifier of the product (e.g., prod-101)", required = true, example = "prod-101")
            @PathVariable String productId) {
        ProductDTO product = new ProductDTO();
        product.setProductId(productId);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Create a new product", description = "Creates a new product in the catalog. Required fields: name, price, category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error - Missing or invalid required fields",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Product with same name already exists in category",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductDTO.class)))
            @RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    @Operation(summary = "Update a product", description = "Updates an existing product. Partial updates are supported.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found - Cannot update non-existent product",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error - Invalid field value (e.g., negative price)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Update would create duplicate product",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Unique identifier of the product to update", required = true, example = "prod-101")
            @PathVariable String productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductDTO.class)))
            @RequestBody ProductDTO productDTO) {
        productDTO.setProductId(productId);
        return ResponseEntity.ok(productDTO);
    }

    @Operation(summary = "Delete a product", description = "Soft deletes a product by marking it as inactive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found - Cannot delete non-existent product",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Product has active orders and cannot be deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Unique identifier of the product to delete", required = true, example = "prod-101")
            @PathVariable String productId) {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search products", description = "Search products by keyword in name or description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching products"),
            @ApiResponse(responseCode = "400", description = "Invalid search query - Query must be at least 2 characters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Search keyword (minimum 2 characters)", required = true, example = "smartphone")
            @RequestParam String query) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(summary = "Get products by category", description = "Retrieves all active products in a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products in category"),
            @ApiResponse(responseCode = "404", description = "Category not found - The specified category does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Category name (e.g., electronics, clothing)", required = true, example = "electronics")
            @PathVariable String category) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(summary = "Get product inventory", description = "Retrieves current inventory/stock information for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product inventory"),
            @ApiResponse(responseCode = "404", description = "Product not found - No inventory data for this product ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{productId}/inventory")
    public ResponseEntity<ProductDTO> getProductInventory(
            @Parameter(description = "Unique identifier of the product", required = true, example = "prod-101")
            @PathVariable String productId) {
        return ResponseEntity.ok(new ProductDTO());
    }

    @Operation(summary = "Get product reviews", description = "Retrieves all customer reviews for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product reviews"),
            @ApiResponse(responseCode = "404", description = "Product not found - Cannot retrieve reviews for non-existent product",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ProductDTO>> getProductReviews(
            @Parameter(description = "Unique identifier of the product", required = true, example = "prod-101")
            @PathVariable String productId) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}