package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.CustomerDTO;
import com.tekion.API_Design_Demo.dto.ProductDTO;
import com.tekion.API_Design_Demo.dto.ReviewDTO;
import com.tekion.API_Design_Demo.dto.request.CreateReviewRequest;
import com.tekion.API_Design_Demo.dto.request.UpdateReviewRequest;
import com.tekion.API_Design_Demo.dto.response.ApiResponse;
import com.tekion.API_Design_Demo.service.DataStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review", description = "Review management APIs")
public class ReviewController {

    private final DataStore dataStore;

    public ReviewController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @GetMapping
    @Operation(summary = "Get all reviews", description = "Returns a list of all reviews with optional filtering")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getAllReviews(
            @Parameter(description = "Filter by minimum rating") @RequestParam(required = false) Integer minRating,
            @Parameter(description = "Filter by maximum rating") @RequestParam(required = false) Integer maxRating) {

        List<ReviewDTO> reviews = dataStore.getReviews().values().stream()
                .filter(r -> minRating == null || r.getRating() >= minRating)
                .filter(r -> maxRating == null || r.getRating() <= maxRating)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review by ID", description = "Returns a single review by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<?> getReviewById(
            @Parameter(description = "Review ID") @PathVariable String reviewId) {
        ReviewDTO review = dataStore.getReview(reviewId);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Review not found with id: " + reviewId));
        }
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PostMapping
    @Operation(summary = "Create a new review",
            description = "Creates a new review for a product by the authenticated customer. Customer ID is obtained from the X-Customer-Id header (simulating authentication context).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Review created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or missing X-Customer-Id header"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer or product not found")
    })
    public ResponseEntity<?> createReview(
            @Parameter(description = "Customer ID (simulates authenticated user context)", required = true, example = "cust-12345678")
            @RequestHeader("X-Customer-Id") String customerId,
            @Valid @RequestBody CreateReviewRequest request) {
        // Validate customer exists (customerId comes from auth header, not request body)
        CustomerDTO customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Customer not found with id: " + customerId));
        }

        // Validate product exists
        ProductDTO product = dataStore.getProduct(request.getProductId());
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Product not found with id: " + request.getProductId()));
        }

        String reviewId = "rev-" + UUID.randomUUID().toString().substring(0, 8);

        ReviewDTO review = ReviewDTO.builder()
                .reviewId(reviewId)
                .customerId(customer.getCustomerId())
                .customerName(customer.getName())
                .productId(product.getProductId())
                .productName(product.getName())
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewDate(LocalDateTime.now())
                .build();

        dataStore.saveReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(review));
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update a review",
            description = "Updates an existing review. Only rating and comment can be modified - the product and customer remain unchanged.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<?> updateReview(
            @Parameter(description = "Review ID", required = true) @PathVariable String reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {

        ReviewDTO existing = dataStore.getReview(reviewId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Review not found with id: " + reviewId));
        }

        // Only update rating and comment - product and customer are immutable
        ReviewDTO updated = ReviewDTO.builder()
                .reviewId(reviewId)
                .customerId(existing.getCustomerId())
                .customerName(existing.getCustomerName())
                .productId(existing.getProductId())
                .productName(existing.getProductName())
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewDate(existing.getReviewDate())
                .build();

        dataStore.saveReview(updated);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review", description = "Deletes a review by its ID")
    public ResponseEntity<?> deleteReview(
            @Parameter(description = "Review ID") @PathVariable String reviewId) {
        ReviewDTO review = dataStore.getReview(reviewId);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Review not found with id: " + reviewId));
        }
        dataStore.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

}
