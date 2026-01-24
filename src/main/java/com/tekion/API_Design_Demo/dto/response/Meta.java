package com.tekion.API_Design_Demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Meta information for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Metadata for API response")
public class Meta {
    
    @Schema(description = "Response status", example = "SUCCESS")
    private String status;
    
    @Schema(description = "Optional message", example = "Inventory retrieved successfully")
    private String message;
    
    @Schema(description = "Total count for paginated responses", example = "100")
    private Integer totalCount;
    
    @Schema(description = "Current page number", example = "1")
    private Integer page;
    
    @Schema(description = "Page size", example = "20")
    private Integer pageSize;
    
    /**
     * Create a success meta with default message
     */
    public static Meta success() {
        return Meta.builder().status("SUCCESS").build();
    }
    
    /**
     * Create a success meta with custom message
     */
    public static Meta success(String message) {
        return Meta.builder().status("SUCCESS").message(message).build();
    }
    
    /**
     * Create a success meta with pagination info
     */
    public static Meta success(int totalCount, int page, int pageSize) {
        return Meta.builder()
                .status("SUCCESS")
                .totalCount(totalCount)
                .page(page)
                .pageSize(pageSize)
                .build();
    }
}

