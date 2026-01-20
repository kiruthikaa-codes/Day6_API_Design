package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review", description = "Review management APIs")
public class ReviewController {

    @GetMapping
    @Operation(summary = "Get all reviews", description = "Returns a list of all reviews")
    public List<ReviewDTO> getAllReviews() {
        return null;
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review by ID", description = "Returns a single review by its ID")
    public ReviewDTO getReviewById(
            @Parameter(description = "Review ID") @PathVariable String reviewId) {
        return null;
    }

    @PostMapping
    @Operation(summary = "Create a new review", description = "Creates a new review and returns it")
    public ReviewDTO createReview(@RequestBody ReviewDTO reviewDTO) {
        return reviewDTO;
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update a review", description = "Updates an existing review by its ID")
    public ReviewDTO updateReview(
            @Parameter(description = "Review ID") @PathVariable String reviewId,
            @RequestBody ReviewDTO reviewDTO) {
        return reviewDTO;
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review", description = "Deletes a review by its ID")
    public void deleteReview(
            @Parameter(description = "Review ID") @PathVariable String reviewId) {
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews by product", description = "Returns all reviews for a specific product")
    public List<ReviewDTO> getReviewsByProduct(
            @Parameter(description = "Product ID") @PathVariable String productId) {
        return null;
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get reviews by customer", description = "Returns all reviews written by a specific customer")
    public List<ReviewDTO> getReviewsByCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {
        return null;
    }
}

