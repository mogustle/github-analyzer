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
            return Objects.requireNonNull(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Repository {}/{} not found", owner, repo, e);
            return null;
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            log.error("Authentication error while accessing repository {}/{}", owner, repo, e);
            throw new GithubAuthenticationException("Authentication failed for GitHub API: " + e.getMessage());
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Rate limit exceeded while accessing repository {}/{}", owner, repo, e);
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

    private HttpEntity<?> createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubApiToken);
        headers.set(HttpHeaders.ACCEPT, ACCEPT_HEADER);
        headers.set(VERSION_HEADER, API_VERSION);
        return new HttpEntity<>(headers);
    }
} 