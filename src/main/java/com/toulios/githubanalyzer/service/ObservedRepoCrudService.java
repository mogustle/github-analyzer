package com.toulios.githubanalyzer.service;

import com.toulios.githubanalyzer.dto.request.ObservedRepoFilter;
import com.toulios.githubanalyzer.dto.request.ObservedRepoRequest;
import com.toulios.githubanalyzer.dto.request.ObservedRepoUpdateRequest;
import com.toulios.githubanalyzer.dto.response.ObservedRepoResponse;
import com.toulios.githubanalyzer.dto.response.PaginatedResponse;
import com.toulios.githubanalyzer.exception.RepoCreationException;
import com.toulios.githubanalyzer.exception.RepoNotFoundException;
import com.toulios.githubanalyzer.model.ObservedRepo;
import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import com.toulios.githubanalyzer.repository.ObservedRepoRepository;
import com.toulios.githubanalyzer.repository.specification.ObservedRepoSpecification;
import com.toulios.githubanalyzer.util.JsonNullableUtils;
import com.toulios.githubanalyzer.util.ObservedRepoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class handling CRUD operations for ObservedRepo entities.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ObservedRepoCrudService {
    private static final String LOG_PREFIX = "[ObservedRepoCrudService]";

    private final ObservedRepoRepository repository;

    /**
     * Creates or updates a repository based on owner and name.
     *
     * @param request the repository data
     * @return the created/updated repository
     * @throws RepoCreationException if creation fails
     */
    @Transactional
    public ObservedRepoResponse insert(ObservedRepoRequest request) {
        try {
            log.info("{} Inserting repository {}/{}", LOG_PREFIX, request.getOwner(), request.getName());

            Optional<ObservedRepo> optionalRepo = repository.findByOwnerAndName(request.getOwner(), request.getName());
            if (optionalRepo.isPresent()) {
                return ObservedRepoMapper.toResponse(optionalRepo.get());
            }

            ObservedRepo repo = ObservedRepoMapper.createEntityFromRequest(request);
            repo = repository.save(repo);

            log.info("{} Successfully inserted repository with id: {}", LOG_PREFIX, repo.getId());
            return ObservedRepoMapper.toResponse(repo);
        } catch (Exception e) {
            log.error("{} Error inserting repository: {}", LOG_PREFIX, e.getMessage(), e);
            throw new RepoCreationException("Failed to create/update repository: " + e.getMessage());
        }
    }

    /**
     * Retrieves a repository by its ID.
     *
     * @param id the repository ID
     * @return the repository data
     * @throws RepoNotFoundException if repository not found
     */
    public ObservedRepoResponse getById(Long id) {
        log.info("{} Fetching repository with id: {}", LOG_PREFIX, id);
        return repository.findById(id)
                .map(ObservedRepoMapper::toResponse)
                .orElseThrow(() -> new RepoNotFoundException("Repository not found with id: " + id));
    }

    /**
     * Lists all repositories with optional filtering in a paginated way.
     *
     * @param filter   filtering criteria
     * @param pageable pagination information
     * @return filtered page of repositories with next/previous URLs
     */
    public PaginatedResponse<ObservedRepoResponse> listAll(ObservedRepoFilter filter, Pageable pageable) {
        log.info("{} Fetching page {} of repositories with filters: {}",
                LOG_PREFIX, pageable.getPageNumber(), filter);

        Specification<ObservedRepo> spec = ObservedRepoSpecification.withFilter(filter);
        Page<ObservedRepoResponse> page = repository.findAll(spec, pageable)
                .map(ObservedRepoMapper::toResponse);

        return PaginatedResponse.from(page, "/api/v1/repos");
    }

    /**
     * Deletes a repository by its ID.
     *
     * @param id the repository ID
     * @throws RepoNotFoundException if repository not found
     */
    @Transactional
    public void deleteById(Long id) {
        log.info("{} Soft deleting repository with id: {}", LOG_PREFIX, id);

        ObservedRepo repo = repository.findById(id)
                .orElseThrow(() -> new RepoNotFoundException("Repository not found with id: " + id));

        repo.setStatus(ObservedRepoStatus.DELETED);
        repository.save(repo);

        log.info("{} Successfully marked repository as deleted with id: {}", LOG_PREFIX, id);
    }

    /**
     * Updates a repository with the given data.
     * Only updates fields that are present in the request.
     *
     * @param id the ID of the repository to update
     * @param request the update data
     * @return the updated repository
     * @throws RepoNotFoundException if the repository is not found
     */
    @Transactional
    public ObservedRepoResponse update(Long id, ObservedRepoUpdateRequest request) {
        log.info("{} Updating repository with id: {}", LOG_PREFIX, id);
        
        ObservedRepo repo = repository.findById(id)
                .orElseThrow(() -> new RepoNotFoundException("Repository not found with id: " + id));

        updateRepoFromRequest(repo, request);
        repo = repository.save(repo);
        
        log.info("{} Successfully updated repository with id: {}", LOG_PREFIX, id);
        return ObservedRepoMapper.toResponse(repo);
    }

    /**
     * Updates a repository from an ObservedRepoUpdateRequest.
     * @param repo the repository to update
     * @param request the update request
     */ 
    private void updateRepoFromRequest(ObservedRepo repo, ObservedRepoUpdateRequest request) {
        JsonNullableUtils.updateIfPresent(request.getName(), repo::setName);
        JsonNullableUtils.updateIfPresent(request.getOwner(), repo::setOwner);
        JsonNullableUtils.updateIfPresent(request.getStars(), repo::setStars);
        JsonNullableUtils.updateIfPresent(request.getOpenIssues(), repo::setOpenIssues);
        JsonNullableUtils.updateIfPresent(request.getUrl(), repo::setUrl);
        JsonNullableUtils.updateIfPresent(request.getStatus(), repo::setStatus);
        JsonNullableUtils.updateIfPresent(request.getLicence(), repo::setLicence);
    }
} 