package com.toulios.consumer.listener;

import com.toulios.consumer.event.RepoChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener class for handling repository change events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RepoChangeListener {
    private static final String LOG_PREFIX = "[RepoChangeListener]";

    /**
     * Handles a repository change event.
     *
     * @param event the repository change event
     */
    @KafkaListener(topics = "${app.kafka.topics.repo-changes}", groupId = "analysis-group")
    public void handleRepoChange(RepoChangeEvent event) {
        log.info("{} Received change event for repository {}: {}", 
                LOG_PREFIX, event.getRepoId(), event.getChanges());
    }
} 