package com.toulios.githubanalyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.cloud.sleuth.annotation.NewSpan;

/**
 * Service to schedule repository import jobs
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ObservedRepoImportScheduler {

    private final static String LOG_PREFIX = "[ObservedRepoImportScheduler]";
    private final ObservedRepoProcessingService observedRepoProcessingService;

    @Scheduled(fixedRateString = "${app.scheduler.fixed-rate}")
    @NewSpan("import-repositories-scheduled")
    public void scheduledObservedRepoImport() {
        try {
            log.info("{} Starting scheduled repository import", LOG_PREFIX);
            observedRepoProcessingService.processObservedRepos();
        } catch (Exception e) {
            log.error("{} Error during repository import: {}", LOG_PREFIX, e.getMessage(), e);
        }
    }
} 