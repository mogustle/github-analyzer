package com.toulios.githubanalyzer.controller;

import com.toulios.githubanalyzer.config.ApiVersionConfig;
import com.toulios.githubanalyzer.dto.request.ObservedRepoFilter;
import com.toulios.githubanalyzer.dto.request.ObservedRepoRequest;
import com.toulios.githubanalyzer.dto.response.ObservedRepoResponse;
import com.toulios.githubanalyzer.dto.response.PaginatedResponse;
import com.toulios.githubanalyzer.service.ObservedRepoCrudService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing GitHub repositories.
 * API Version: 1
 */
@RestController
@RequestMapping(ApiVersionConfig.BASE_API_PATH + "/observed-repos")
@RequiredArgsConstructor
@Tag(name = "Repository Management", description = "Endpoints for managing observed GitHub repositories")
public class ObservedRepoController {

    private final ObservedRepoCrudService service;

    @Operation(
            summary = "Create or update repository",
            description = "Creates a new repository or updates existing one if it exists (based on owner and name)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Repository created/updated successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ObservedRepoResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
    )
    @PostMapping
    @RateLimiter(name = "observedRepoApi")
    public ResponseEntity<ObservedRepoResponse> create(@Valid @RequestBody ObservedRepoRequest request) {
        return ResponseEntity.ok(service.insert(request));
    }

    @Operation(
            summary = "Get repository by ID",
            description = "Retrieves a repository by its unique identifier"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Repository found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ObservedRepoResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Repository not found",
            content = @Content
    )
    @GetMapping("/{id}")
    public ResponseEntity<ObservedRepoResponse> getById(
            @Parameter(description = "Repository ID") @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "List all repositories",
            description = "Retrieves all repositories in a paginated way with optional filtering"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Repositories retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PaginatedResponse.class)
            )
    )
    @Parameters({
            @Parameter(
                    name = "owner",
                    description = "Filter by repository owner",
                    in = ParameterIn.QUERY
            ),
            @Parameter(
                    name = "name",
                    description = "Filter by repository name",
                    in = ParameterIn.QUERY
            ),
            @Parameter(
                    name = "status",
                    description = "Filter by repository status",
                    in = ParameterIn.QUERY
            ),
            @Parameter(
                    name = "licence",
                    description = "Filter by repository licence",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping
    @RateLimiter(name = "observedRepoApi")
    public ResponseEntity<PaginatedResponse<ObservedRepoResponse>> listAll(
            @Parameter(hidden = true) ObservedRepoFilter filter,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(service.listAll(filter, pageable));
    }

    @Operation(
            summary = "Delete repository",
            description = "Deletes a repository by its ID"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Repository deleted successfully",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Repository not found",
            content = @Content
    )
    @DeleteMapping("/{id}")
    @RateLimiter(name = "observedRepoApi")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Repository ID") @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 