package com.toulios.githubanalyzer.dto.request;

import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import lombok.Builder;
import lombok.Data;

/**
 * DTO class for observed repository filter
 */
@Data
@Builder
public class ObservedRepoFilter {
    private String owner;
    private String name;
    private ObservedRepoStatus status;
    private String licence;
} 