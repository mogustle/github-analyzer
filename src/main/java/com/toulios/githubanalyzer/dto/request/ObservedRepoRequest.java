package com.toulios.githubanalyzer.dto.request;

import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * DTO class for observed repository request
 */
@Data
public class ObservedRepoRequest {
    @NotBlank(message = "Repository name is required")
    @Size(max = 1000, message = "Repository name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Owner is required")
    @Size(max = 1000, message = "Owner name cannot exceed 100 characters")
    private String owner;

    @Size(max = 100, message = "license cannot exceed 100 characters")
    private String license;

    @Min(value = 0, message = "Stars should be greater than zero")
    private Integer stars;

    @Min(value = 0, message = "Stars should be greater than zero")
    private Integer openIssues;

    @URL
    private String url;

    private ObservedRepoStatus status;
} 