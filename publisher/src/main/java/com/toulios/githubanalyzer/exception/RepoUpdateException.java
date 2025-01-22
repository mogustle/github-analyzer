package com.toulios.githubanalyzer.exception;

/**
 * Exception thrown when there is an error updating a repository.
 */
public class RepoUpdateException extends RuntimeException {
    public RepoUpdateException(String message) {
        super(message);
    }
} 