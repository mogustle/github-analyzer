package com.toulios.githubanalyzer.service;

import com.toulios.githubanalyzer.client.GithubApiClient;
import com.toulios.githubanalyzer.dto.GithubRepositoryDto;
import com.toulios.githubanalyzer.dto.request.ObservedRepoFilter;
import com.toulios.githubanalyzer.model.ObservedRepo;
import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import com.toulios.githubanalyzer.repository.ObservedRepoRepository;
import com.toulios.githubanalyzer.repository.specification.ObservedRepoSpecification;
import com.toulios.githubanalyzer.util.ObservedRepoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing and processing GitHub repositories stored in the database.
 * This service provides functionality to:
 * - Process repositories in a paginated manner
 * - Fetch updated repository information from GitHub API
 * - Handle repository data updates
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ObservedRepoProcessingService {

    private final static String LOG_PREFIX = "[ObservedRepoService]";
    private static final int PAGE_SIZE = 100;

    private final GithubApiClient githubApiClient;
    private final ObservedRepoRepository observedRepoRepository;
    private final ObservedRepoHelper observedRepoHelper;

    /**
     * Processes all repositories stored in the database in a paginated manner.
     * For each repository, fetches updated information from GitHub API.
     * Handles pagination automatically and processes repositories in batches to avoid memory issues.
     *
     * @throws RuntimeException if there's an unrecoverable error during processing
     */
    public void processObservedRepos() {
        log.info("{} Starting to process all repositories with page size: {}", LOG_PREFIX, PAGE_SIZE);
        int pageNumber = 0;
        long totalProcessed = 0;
        ObservedRepoFilter filter = ObservedRepoFilter
                .builder()
                .status(ObservedRepoStatus.ACTIVE)
                .build();
        Specification<ObservedRepo> spec = ObservedRepoSpecification.withFilter(filter);
        Page<ObservedRepo> page = loadRepositoryPage(spec, pageNumber);

        if (page.getTotalElements() == 0) {
            log.warn("{} No repositories found in database. Processing skipped.", LOG_PREFIX);
            return;
        }

        log.info("{} Total pages: {}", LOG_PREFIX, page.getTotalPages());

        while (!page.isEmpty()) {
            processObservedRepoPage(page);
            totalProcessed += page.getNumberOfElements();
            log.info("{} Progress: processed {}/{} repositories", LOG_PREFIX, totalProcessed, page.getTotalElements());

            pageNumber++;
            page = loadRepositoryPage(spec, pageNumber);
        }

        log.info("{} Finished processing all repositories. Total processed: {}", LOG_PREFIX, totalProcessed);
    }

    /**
     * Loads a single page of repositories from the database.
     *
     * @param pageNumber the zero-based page number to load
     * @return Page containing repositories, may be empty if no more data is available
     */
    private Page<ObservedRepo> loadRepositoryPage(Specification<ObservedRepo> spec, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        Page<ObservedRepo> page = observedRepoRepository.findAll(spec, pageable);

        if (page.isEmpty()) {
            log.info("{} No more repositories to process", LOG_PREFIX);
        } else {
            log.info("{} Successfully loaded page {} with {} repositories", LOG_PREFIX, pageNumber, page.getNumberOfElements());
        }

        return page;
    }

    /**
     * Processes a single repository by fetching its updated information from GitHub API.
     * Handles any errors that occur during processing of individual repositories.
     *
     * @param repo the repository to process
     */
    private void processRepository(ObservedRepo repo, List<ObservedRepo> repos) {
        try {
            log.info("{} Processing repository: {}/{}", LOG_PREFIX, repo.getOwner(), repo.getName());
            GithubRepositoryDto githubRepo = githubApiClient.getRepositoryDetails(repo.getOwner(), repo.getName());
            if (githubRepo == null) {
                repo.setStatus(ObservedRepoStatus.INVALID);
                repos.add(repo);
                return;
            }

            ObservedRepo updatedRepo = ObservedRepoMapper.toEntity(githubRepo, repo.getId());
            repos.add(updatedRepo);

            observedRepoHelper.handleChanges(repo, updatedRepo);
        } catch (Exception e) {
            log.error("{} Error processing repository {}/{}: {}", LOG_PREFIX, repo.getOwner(), repo.getName(), e.getMessage(), e);
        }
    }

    /**
     * Processes a page of repositories.
     *
     * @param page the page of repositories to process
     */
    private void processObservedRepoPage(Page<ObservedRepo> page) {
        log.info("{} Processing page containing {} repositories", LOG_PREFIX, page.getNumberOfElements());
        List<ObservedRepo> repos = new ArrayList<>();
        page.getContent().forEach((repo) -> processRepository(repo, repos));
        observedRepoRepository.saveAll(repos);
        log.info("{} Completed processing page of {} repositories", LOG_PREFIX, page.getNumberOfElements());
    }
}
