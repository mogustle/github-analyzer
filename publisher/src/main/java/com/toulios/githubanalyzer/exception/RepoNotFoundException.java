package com.toulios.githubanalyzer.exception;

/**
 * Exception thrown when a repository is not found
 */
public class RepoNotFoundException extends RuntimeException {
    public RepoNotFoundException(String message) {
        super(message);
    }
} 