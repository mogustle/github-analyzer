package com.toulios.githubanalyzer.client;

import com.toulios.githubanalyzer.dto.GithubRepositoryDto;
import com.toulios.githubanalyzer.exception.GithubApiException;
import com.toulios.githubanalyzer.exception.GithubAuthenticationException;
import com.toulios.githubanalyzer.exception.RepoNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * Client for interacting with GitHub API
 * Uses Link header pagination to fetch public repositories
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GithubApiClient {
    private static final String API_VERSION = "2022-11-28";
    private static final String ACCEPT_HEADER = "application/vnd.github+json";
    private static final String VERSION_HEADER = "X-GitHub-Api-Version";
    private static final String RATE_LIMIT_LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";

    private final RestTemplate restTemplate;

    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    @Value("${github.api.token}")
    private String githubApiToken;

    public GithubApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches repository details from GitHub API
     *
     * @param owner Owner of the repository
     * @param repo  Name of the repository
     * @return Repository details
     * @throws RepoNotFoundException         if the repository doesn't exist or is not accessible
     * @throws GithubApiException            if there's an API error (rate limit exceeded, server error, etc.)
     * @throws GithubAuthenticationException if there's an authentication error with the GitHub API
     */
    public GithubRepositoryDto getRepositoryDetails(String owner, String repo) {
        String url = githubApiBaseUrl + "/repos/" + owner + "/" + repo;

        try {
            ResponseEntity<GithubRepositoryDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createHeaders(),
                    new ParameterizedTypeReference<>() {
                    }
            );

            // Log rate limit information
            logRateLimitInfo(response);

            return Objects.requireNonNull(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Repository {}/{} not found", owner, repo, e);
            return null;
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            log.error("Authentication error while accessing repository {}/{}", owner, repo, e);
            throw new GithubAuthenticationException("Authentication failed for GitHub API: " + e.getMessage());
        } catch (HttpClientErrorException.TooManyRequests e) {
            // Enhanced rate limit exceeded logging
            log.error("Rate limit exceeded while accessing repository {}/{}. Reset time: {}",
                    owner, repo, getRateLimitResetTime(e.getResponseHeaders()), e);
            throw new GithubApiException("GitHub API rate limit exceeded. Please try again later.");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("GitHub API error while accessing repository {}/{}", owner, repo, e);
            throw new GithubApiException("GitHub API error: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Network error while accessing repository {}/{}", owner, repo, e);
            throw new GithubApiException("Failed to connect to GitHub API: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while accessing repository {}/{}", owner, repo, e);
            throw new GithubApiException("Unexpected error occurred while accessing GitHub API");
        }
    }

    private void logRateLimitInfo(ResponseEntity<?> response) {
        HttpHeaders headers = response.getHeaders();
        String limit = headers.getFirst(RATE_LIMIT_LIMIT_HEADER);
        String remaining = headers.getFirst(RATE_LIMIT_REMAINING_HEADER);
        String reset = headers.getFirst(RATE_LIMIT_RESET_HEADER);

        if (limit != null && remaining != null && reset != null) {
            String resetTime = formatResetTime(reset);
            log.debug("GitHub API Rate Limit - Limit: {}, Remaining: {}, Resets at: {}",
                    limit, remaining, resetTime);

            // Warn if remaining requests are low (less than 10% of limit)
            int remainingRequests = Integer.parseInt(remaining);
            int rateLimit = Integer.parseInt(limit);
            if (remainingRequests < (rateLimit * 0.1)) {
                log.warn("GitHub API Rate Limit is running low! Remaining: {} out of {}",
                        remaining, limit);
            }
        }
    }

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

    private String getRateLimitResetTime(HttpHeaders headers) {
        if (headers != null) {
            String reset = headers.getFirst(RATE_LIMIT_RESET_HEADER);
            return reset != null ? formatResetTime(reset) : "unknown";
        }
        return "unknown";
    }

    private HttpEntity<?> createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubApiToken);
        headers.set(HttpHeaders.ACCEPT, ACCEPT_HEADER);
        headers.set(VERSION_HEADER, API_VERSION);
        return new HttpEntity<>(headers);
    }
} 