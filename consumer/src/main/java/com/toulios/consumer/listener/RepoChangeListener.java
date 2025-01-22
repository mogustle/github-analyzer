package com.toulios.consumer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;
    /**
     * Handles a repository change event.
     *
     * @param event the repository change event
     */
    @KafkaListener(topics = "${app.kafka.topics.repo-changes}", 
                  groupId = "analysis-group",
                  containerFactory = "kafkaListenerContainerFactory")
    public void handleRepoChange(String event) {
        try {
            RepoChangeEvent repoChangeEvent = objectMapper.readValue(event, RepoChangeEvent.class);

            log.info("{} Received change event at:[{}] for repository {}: {}",
                    LOG_PREFIX, repoChangeEvent.getTimestamp(), repoChangeEvent.getRepoId(), repoChangeEvent.getChanges());
        } catch (Exception e) {
            log.error("{} Error deserializing event: {}", LOG_PREFIX, event, e);
        }
    }
} 