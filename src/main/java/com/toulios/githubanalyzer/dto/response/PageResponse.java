package com.toulios.githubanalyzer.dto.response;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic wrapper for paginated API responses.
 * Provides standardized structure for returning paginated data.
 *
 * @param <T> type of content being paginated
 */
@Value
public class PageResponse<T> {
    /** Current page content */
    List<T> content;
    
    /** Total number of elements across all pages */
    long totalElements;
    
    /** Total number of pages */
    int totalPages;
    
    /** Current page number (0-based) */
    int pageNumber;
    
    /** Number of elements per page */
    int pageSize;

    /**
     * Creates a PageResponse from a Spring Page object.
     *
     * @param page Spring Page instance
     * @return new PageResponse instance
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        );
    }
} 