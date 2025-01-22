package com.toulios.githubanalyzer.exception;

/**
 * Exception thrown when there's an error with the GitHub API.
 */
public class GithubApiException extends RuntimeException {
    public GithubApiException(String message) {
        super(message);
    }
} 