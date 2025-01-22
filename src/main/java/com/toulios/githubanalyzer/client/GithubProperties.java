package com.toulios.githubanalyzer.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubProperties {
    /**
     * Default rate limit for GitHub API (requests per hour)
     */
    private int defaultLimit;

    /**
     * Default window duration in seconds
     */
    private int defaultWindowSeconds;

    /**
     * Buffer of requests to keep available (percentage of limit)
     */
    private int bufferPercentage;

    /**
     * Base URL for the GitHub API
     */
    private String githubApiBaseUrl;

    /**
     * Token for the GitHub API
     */
    private String githubApiToken;
} 