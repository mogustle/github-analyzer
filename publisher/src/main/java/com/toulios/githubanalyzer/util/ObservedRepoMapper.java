package com.toulios.githubanalyzer.util;

import com.toulios.githubanalyzer.dto.GithubRepositoryDto;
import com.toulios.githubanalyzer.dto.LicenseDto;
import com.toulios.githubanalyzer.dto.OwnerDto;
import com.toulios.githubanalyzer.dto.request.ObservedRepoRequest;
import com.toulios.githubanalyzer.dto.response.ObservedRepoResponse;
import com.toulios.githubanalyzer.model.ObservedRepo;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class for mapping between DTOs and entities
 */
@UtilityClass
public class ObservedRepoMapper {
    /**
     * Maps GitHub API DTO to internal Repository entity
     * Only maps the fields we're interested in storing
     */
    public static ObservedRepo toEntity(GithubRepositoryDto dto, Long id) {
        ObservedRepo observedRepo = new ObservedRepo();
        observedRepo.setId(id);
        observedRepo.setName(dto.getName());
        observedRepo.setUrl(dto.getUrl());
        observedRepo.setOpenIssues(dto.getOpenIssues());
        observedRepo.setStars(dto.getStars());

        String ownerLogin = getNestedProperty(dto.getOwner(), OwnerDto::getLogin);
        String license = getNestedProperty(dto.getLicense(), LicenseDto::getName);

        observedRepo.setOwner(ownerLogin);
        observedRepo.setLicence(license);

        return observedRepo;
    }
    
    /**
     * Maps internal Repository entity to DTO for response
     */
    public static ObservedRepoResponse toResponse(ObservedRepo entity) {
        ObservedRepoResponse response = new ObservedRepoResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setOwner(entity.getOwner());
        response.setStars(entity.getStars());
        response.setOpenIssues(entity.getOpenIssues());
        response.setUrl(entity.getUrl());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setLicense(entity.getLicence());
        response.setStatus(entity.getStatus());
        return response;
    }

    /**
     * Maps ObservedRepoRequest to ObservedRepo entity
     */
    public static ObservedRepo createEntityFromRequest(ObservedRepoRequest request) {
        ObservedRepo entity = new ObservedRepo();
        entity.setName(request.getName());
        entity.setOwner(request.getOwner());
        entity.setStars(request.getStars());
        entity.setOpenIssues(request.getOpenIssues());
        entity.setUrl(request.getUrl());
        entity.setLicence(request.getLicense());
        return entity;
    }

    /**
     * Helper method to get nested property from an object using a mapper function
     */
    private static <T, R> R getNestedProperty(T object, Function<T, R> mapper) {
        return Optional.ofNullable(object)
                .map(mapper)
                .orElseGet(() -> null);
    }
} 