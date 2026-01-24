package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic wrapper for cursor-based paginated responses.
 * Reusable across all entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated response wrapper for cursor-based pagination")
public class PagedResponse<T> {

    @Schema(description = "List of data items in the current page")
    private List<T> data;

    @Schema(description = "Pagination metadata")
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pagination metadata for cursor-based navigation")
    public static class Pagination {
        @Schema(description = "Cursor for fetching the next page of results", example = "eyJpZCI6MTAwfQ==")
        private String nextCursor;

        @Schema(description = "Whether there are more results available", example = "true")
        private boolean hasNext;

        @Schema(description = "Number of items per page", example = "20")
        private int limit;
    }

    // Factory method for convenience
    public static <T> PagedResponse<T> of(List<T> data, String nextCursor, boolean hasNext, int limit) {
        return new PagedResponse<>(data, new Pagination(nextCursor, hasNext, limit));
    }
}

