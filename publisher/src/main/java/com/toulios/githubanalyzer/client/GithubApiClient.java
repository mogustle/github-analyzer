package com.toulios.githubanalyzer.client;

import com.toulios.githubanalyzer.dto.GithubRepositoryDto;
import com.toulios.githubanalyzer.exception.GithubApiException;
import com.toulios.githubanalyzer.exception.GithubAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Client for interacting with the GitHub REST API.
 * The client implements rate limiting using {@link GithubRateLimiter} to prevent exceeding
 * GitHub's API rate limits (5000 requests per hour for authenticated users).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GithubApiClient {
    private final static String LOG_PREFIX = "[GithubApiClient]";

    /** GitHub API version header value */
    private static final String API_VERSION = "2022-11-28";
    /** GitHub API accept header value for JSON responses */
    private static final String ACCEPT_HEADER = "application/vnd.github+json";
    /** GitHub API version header name */
    private static final String VERSION_HEADER = "X-GitHub-Api-Version";
    /** Header containing the total rate limit */
    private static final String RATE_LIMIT_LIMIT_HEADER = "X-RateLimit-Limit";
    /** Header containing remaining requests allowed */
    private static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";
    /** Header containing the rate limit reset timestamp */
    private static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";
    /** Format for the GitHub repository details URL */
    private static final String GITLAB_REPO_DETAILS_URL_FORMAT = "%s/repos/%s/%s";
    private final RestTemplate restTemplate;
    private final GithubRateLimiter rateLimiter;
    private final GithubProperties githubProperties;

    /**
     * Fetches repository details from GitHub API for a specific repository.
     * This method includes rate limiting and will wait if necessary to respect GitHub's rate limits.
     *
     * @param owner The GitHub username or organization name that owns the repository
     * @param repo The name of the repository
     * @return Repository details wrapped in {@link GithubRepositoryDto}, or null if repository is not found
     * @throws GithubAuthenticationException if there are authentication issues with the GitHub API
     * @throws GithubApiException if there are any API errors (rate limit exceeded, server errors, etc.)
     */
    public GithubRepositoryDto getRepositoryDetails(String owner, String repo) {

        String url = format(GITLAB_REPO_DETAILS_URL_FORMAT, githubProperties.getGithubApiBaseUrl(), owner, repo);
        log.debug("{} Getting repo details from {}", LOG_PREFIX, url);

        try {
            // Wait if we're about to exceed rate limits
            rateLimiter.waitIfNeeded();

            ResponseEntity<GithubRepositoryDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createHeaders(),
                    new ParameterizedTypeReference<>() {}
            );

            // Update rate limit information
            updateRateLimits(response.getHeaders());
            
            return Objects.requireNonNull(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            log.error("{} Repository {}/{} not found", LOG_PREFIX, owner, repo, e);
            return null;
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            log.error("{} Authentication error while accessing repository {}/{}", LOG_PREFIX, owner, repo, e);
            throw new GithubAuthenticationException("Authentication failed for GitHub API: " + e.getMessage());
        } catch (HttpClientErrorException.TooManyRequests e) {
            // Update rate limits from error response
            updateRateLimits(e.getResponseHeaders());
            log.error("{} Rate limit exceeded while accessing repository {}/{}. Reset time: {}", 
                    LOG_PREFIX, owner, repo, getRateLimitResetTime(e.getResponseHeaders()), e);
            throw new GithubApiException("GitHub API rate limit exceeded. Please try again later.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GithubApiException("Rate limiting wait was interrupted");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("{} GitHub API error while accessing repository {}/{}", LOG_PREFIX, owner, repo, e);
            throw new GithubApiException("GitHub API error: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("{} Network error while accessing repository {}/{}", LOG_PREFIX, owner, repo, e);
            throw new GithubApiException("Failed to connect to GitHub API: " + e.getMessage());
        } catch (Exception e) {
            log.error("{} Unexpected error while accessing repository {}/{}", LOG_PREFIX, owner, repo, e);
            throw new GithubApiException("Unexpected error occurred while accessing GitHub API");
        }
    }

    /**
     * Updates the rate limiter with the latest rate limit information from GitHub API headers.
     *
     * @param headers The HTTP headers from the GitHub API response
     */
    private void updateRateLimits(HttpHeaders headers) {
        if (headers != null) {
            String limitStr = headers.getFirst(RATE_LIMIT_LIMIT_HEADER);
            String remainingStr = headers.getFirst(RATE_LIMIT_REMAINING_HEADER);
            String resetStr = headers.getFirst(RATE_LIMIT_RESET_HEADER);

            if (limitStr != null && remainingStr != null && resetStr != null) {
                try {
                    int limit = Integer.parseInt(limitStr);
                    int remaining = Integer.parseInt(remainingStr);
                    Instant resetTime = Instant.ofEpochSecond(Long.parseLong(resetStr));
                    
                    rateLimiter.updateRateLimits(remaining, limit, resetTime);
                    
                    log.debug("{} Updated rate limits - Remaining: {}, Limit: {}, Reset: {}", 
                            LOG_PREFIX, remaining, limit, formatResetTime(resetStr));
                } catch (NumberFormatException e) {
                    log.warn("{} Failed to parse rate limit headers", LOG_PREFIX, e);
                }
            }
        }
    }

    /**
     * Formats the rate limit reset timestamp into a human-readable datetime string.
     *
     * @param resetTimestamp The epoch timestamp from GitHub API
     * @return A formatted datetime string in ISO local date time format
     */
    private String formatResetTime(String resetTimestamp) {
        try {
            long epochSeconds = Long.parseLong(resetTimestamp);
            return Instant.ofEpochSecond(epochSeconds)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (NumberFormatException e) {
            return resetTimestamp;
        }
    }

    /**
     * Extracts and formats the rate limit reset time from response headers.
     *
     * @param headers The HTTP headers containing rate limit information
     * @return Formatted reset time or "unknown" if headers are missing or invalid
     */
    private String getRateLimitResetTime(HttpHeaders headers) {
        if (headers != null) {
            String reset = headers.getFirst(RATE_LIMIT_RESET_HEADER);
            return reset != null ? formatResetTime(reset) : "unknown";
        }
        return "unknown";
    }

    /**
     * Creates HTTP headers required for GitHub API authentication and versioning.
     *
     * @return HttpEntity containing the required headers
     */
    private HttpEntity<?> createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubProperties.getGithubApiToken());
        headers.set(HttpHeaders.ACCEPT, ACCEPT_HEADER);
        headers.set(VERSION_HEADER, API_VERSION);
        return new HttpEntity<>(headers);
    }
} 