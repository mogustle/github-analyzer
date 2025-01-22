package com.toulios.githubanalyzer.exception;

/**
 * Exception thrown when there's an authentication error with the GitHub API.
 */
public class GithubAuthenticationException extends RuntimeException {
    public GithubAuthenticationException(String message) {
        super(message);
    }
} 