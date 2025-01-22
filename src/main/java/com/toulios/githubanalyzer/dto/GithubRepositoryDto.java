package com.toulios.githubanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO class for GitHub API repository response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GithubRepositoryDto {
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("html_url")
    private String url;

    private String description;

    private String language;

    private OwnerDto owner;

    @JsonProperty("stargazers_count")
    private Integer stars;

    @JsonProperty("open_issues_count")
    private Integer openIssues;

    private LicenseDto license;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}