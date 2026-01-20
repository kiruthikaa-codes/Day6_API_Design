package com.tekion.API_Design_Demo.dto;

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
public class PagedResponse<T> {

    private List<T> data;
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private String nextCursor;
        private boolean hasNext;
        private int limit;
    }

    // Factory method for convenience
    public static <T> PagedResponse<T> of(List<T> data, String nextCursor, boolean hasNext, int limit) {
        return new PagedResponse<>(data, new Pagination(nextCursor, hasNext, limit));
    }
}

