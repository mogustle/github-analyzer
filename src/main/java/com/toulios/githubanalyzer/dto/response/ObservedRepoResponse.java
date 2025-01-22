package com.toulios.githubanalyzer.dto.response;

import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO class for observed repository response
 */
@Data
public class ObservedRepoResponse {
    private Long id;
    private String name;
    private String owner;
    private Integer stars;
    private Integer openIssues;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String license;
    private ObservedRepoStatus status;
} 