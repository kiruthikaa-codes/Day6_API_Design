package com.tekion.API_Design_Demo.controller;

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
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {

    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all products")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable String productId) {
        ProductDTO product = new ProductDTO();
        product.setProductId(productId);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Create a new product", description = "Creates a new product in the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "Product details to create", required = true)
            @RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    @Operation(summary = "Update a product", description = "Updates an existing product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Unique identifier of the product to update", required = true)
            @PathVariable String productId,
            @Parameter(description = "Updated product details", required = true)
            @RequestBody ProductDTO productDTO) {
        productDTO.setProductId(productId);
        return ResponseEntity.ok(productDTO);
    }

    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Unique identifier of the product to delete", required = true)
            @PathVariable String productId) {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search products", description = "Search products by keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching products"),
            @ApiResponse(responseCode = "400", description = "Invalid search query", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam String query) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(summary = "Get products by category", description = "Retrieves all products in a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products by category"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Category name", required = true)
            @PathVariable String category) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Operation(summary = "Get product inventory", description = "Retrieves inventory information for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product inventory"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{productId}/inventory")
    public ResponseEntity<ProductDTO> getProductInventory(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable String productId) {
        return ResponseEntity.ok(new ProductDTO());
    }

    @Operation(summary = "Get product reviews", description = "Retrieves all reviews for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product reviews"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ProductDTO>> getProductReviews(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable String productId) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}