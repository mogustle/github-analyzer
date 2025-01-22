package com.toulios.consumer.listener;

import com.toulios.consumer.event.RepoChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class RepoChangeListenerTest {

    @InjectMocks
    private RepoChangeListener repoChangeListener;

    @BeforeEach
    void setUp() {
        // Add any setup if needed in the future
    }

    @Test
    void handleRepoChange_ShouldProcessEventSuccessfully() {
        // Given
        RepoChangeEvent event = RepoChangeEvent.builder()
                .repoId(1L)
                .changes("changes updated")
                .build();

        // When & Then
        assertDoesNotThrow(() -> repoChangeListener.handleRepoChange(event));
    }
} 