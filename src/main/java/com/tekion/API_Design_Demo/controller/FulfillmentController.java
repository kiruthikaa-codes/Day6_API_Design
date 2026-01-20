package com.tekion.API_Design_Demo.controller;

import com.tekion.API_Design_Demo.dto.FulfillmentDTO;
import com.tekion.API_Design_Demo.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fulfillments")
@Tag(name = "Fulfillment", description = "Fulfillment management APIs")
public class FulfillmentController {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    @Operation(
            summary = "Get all fulfillments",
            description = "Retrieves fulfillments with cursor-based pagination. Optionally filter by orderId."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved fulfillments")
    })
    @GetMapping
    public ResponseEntity<PagedResponse<FulfillmentDTO>> getFulfillments(
            @Parameter(description = "Filter by order ID")
            @RequestParam(required = false) String orderId,

            @Parameter(description = "Cursor from previous response for pagination")
            @RequestParam(required = false) String cursor,

            @Parameter(description = "Number of items per page (max: 100)")
            @RequestParam(defaultValue = "20") int limit) {

        limit = Math.min(limit, MAX_LIMIT);
        return ResponseEntity.ok(PagedResponse.of(List.of(), null, false, limit));
    }

    @Operation(
            summary = "Get fulfillment by ID",
            description = "Retrieves a single fulfillment by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fulfillment found"),
            @ApiResponse(responseCode = "404", description = "Fulfillment not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FulfillmentDTO> getFulfillmentById(
            @Parameter(description = "Fulfillment ID", required = true)
            @PathVariable("id") String fulfillmentId) {
        return ResponseEntity.ok(new FulfillmentDTO());
    }

    @Operation(
            summary = "Create a new fulfillment",
            description = "Creates a new fulfillment. Server generates fulfillmentId and timestamps."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fulfillment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<FulfillmentDTO> createFulfillment(
            @Valid @RequestBody FulfillmentDTO fulfillmentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fulfillmentDTO);
    }

    @Operation(
            summary = "Update fulfillment partially",
            description = "Updates specific fields of a fulfillment. Only 'status' can be updated."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fulfillment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Fulfillment not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<FulfillmentDTO> patchFulfillment(
            @Parameter(description = "Fulfillment ID", required = true)
            @PathVariable("id") String fulfillmentId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fields to update",
                    content = @Content(schema = @Schema(example = "{\"status\": \"COMPLETED\"}"))
            )
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(new FulfillmentDTO());
    }
}