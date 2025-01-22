package com.toulios.githubanalyzer.client;

/**
 * Custom exception for GitHub API related errors
 */
public class GithubApiException extends RuntimeException {
    public GithubApiException(String message) {
        super(message);
    }

    public GithubApiException(String message, Throwable cause) {
        super(message, cause);
    }
} 