package com.toulios.githubanalyzer.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * A response object that contains a paginated list of results.
 * @param <T> the type of the results
 */
@Data
@Builder
public class PaginatedResponse<T> {
    private String next;
    private String previous;
    private List<T> results;

    /**
     * Creates a PaginatedResponse from a Page object.
     * @param page the page to create the response from
     * @param baseUrl the base URL for the API
     * @return the PaginatedResponse
     */
    public static <T> PaginatedResponse<T> from(Page<T> page, String baseUrl) {
        return PaginatedResponse.<T>builder()
                .next(createNextPageUrl(page, baseUrl))
                .previous(createPreviousPageUrl(page, baseUrl))
                .results(page.getContent())
                .build();
    }

    /**
     * Creates the URL for the next page.
     * @param page the page to create the URL for
     * @param baseUrl the base URL for the API
     * @return the URL for the next page
     */
    private static <T> String createNextPageUrl(Page<T> page, String baseUrl) {
        if (!page.hasNext()) {
            return null;
        }
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("page", page.getNumber() + 1)
                .queryParam("size", page.getSize())
                .build()
                .toUriString();
    }

    /**
     * Creates the URL for the previous page.
     * @param page the page to create the URL for
     * @param baseUrl the base URL for the API
     * @return the URL for the previous page
     */
    private static <T> String createPreviousPageUrl(Page<T> page, String baseUrl) {
        if (!page.hasPrevious()) {
            return null;
        }
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("page", page.getNumber() - 1)
                .queryParam("size", page.getSize())
                .build()
                .toUriString();
    }
} 