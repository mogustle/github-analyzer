package com.toulios.githubanalyzer.dto.request;

import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Request object for updating a ObservedRepo entity.
 */
@Data
public class ObservedRepoUpdateRequest {
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<String> owner = JsonNullable.undefined();
    private JsonNullable<Integer> stars = JsonNullable.undefined();
    private JsonNullable<Integer> openIssues = JsonNullable.undefined();
    private JsonNullable<String> url = JsonNullable.undefined();
    private JsonNullable<ObservedRepoStatus> status = JsonNullable.undefined();
    private JsonNullable<String> licence = JsonNullable.undefined();
} 