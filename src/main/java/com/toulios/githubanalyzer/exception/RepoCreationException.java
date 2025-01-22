package com.toulios.githubanalyzer.exception;

/**
 * Exception thrown when there is an error creating a repository.
 */
public class RepoCreationException extends RuntimeException {
    public RepoCreationException(String message) {
        super(message);
    }
} 