package com.toulios.githubanalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservedRepoImportSchedulerTest {

    @Mock
    private ObservedRepoProcessingService observedRepoProcessingService;

    private ObservedRepoImportScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new ObservedRepoImportScheduler(observedRepoProcessingService);
    }

    @Test
    void scheduledObservedRepoImport_ShouldCallProcessObservedRepos() {
        // When
        scheduler.scheduledObservedRepoImport();

        // Then
        verify(observedRepoProcessingService, times(1)).processObservedRepos();
    }

    @Test
    void scheduledObservedRepoImport_ShouldHandleException() {
        // Given
        doThrow(new RuntimeException("Test exception"))
            .when(observedRepoProcessingService).processObservedRepos();

        // When
        scheduler.scheduledObservedRepoImport();

        // Then
        verify(observedRepoProcessingService, times(1)).processObservedRepos();
        // Test passes if no exception is thrown
    }
} 