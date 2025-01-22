package com.toulios.githubanalyzer.config;

import com.toulios.githubanalyzer.client.GithubProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the application.
 */
@Configuration
public class GithubConfig {

    /**
     * Default rate limit for GitHub API (requests per hour)
     */
    @Value("${github.api.rate-limit.default-limit:5000}")
    private int defaultLimit;

    /**
     * Default window duration in seconds
     */
    @Value("${github.api.rate-limit.default-window-seconds:3600}")
    private int defaultWindowSeconds;

    /**
     * Buffer of requests to keep available (percentage of limit)
     * Default is 5% of total limit
     */
    @Value("${github.api.rate-limit.buffer-percentage:5}")
    private int bufferPercentage;

    /**
     * Base URL for the GitHub API
     */
    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    /**
     * Token for the GitHub API
     */
    @Value("${github.api.token}")
    private String githubApiToken;

    /**
     * Bean for the GitHub rate limit properties.
     * @return the GitHub rate limit properties
     */
    @Bean
    public GithubProperties githubRateLimitProperties() {
        return new GithubProperties(defaultLimit, defaultWindowSeconds, bufferPercentage, githubApiBaseUrl, githubApiToken);
    }

}
