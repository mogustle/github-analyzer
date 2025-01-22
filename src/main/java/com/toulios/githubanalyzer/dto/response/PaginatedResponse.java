package com.toulios.githubanalyzer.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Data
@Builder
public class PaginatedResponse<T> {
    private String next;
    private String previous;
    private List<T> results;

    public static <T> PaginatedResponse<T> from(Page<T> page, String baseUrl) {
        return PaginatedResponse.<T>builder()
                .next(createNextPageUrl(page, baseUrl))
                .previous(createPreviousPageUrl(page, baseUrl))
                .results(page.getContent())
                .build();
    }

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