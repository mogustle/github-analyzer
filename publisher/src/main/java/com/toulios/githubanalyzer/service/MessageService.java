package com.toulios.githubanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toulios.githubanalyzer.event.RepoChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class handling message sending to Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private static final String LOG_PREFIX = "[MessageService]";
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Sends a repository change event to Kafka.
     *
     * @param repoId ID of the repository that changed
     * @param changes description of the changes
     */
    public void sendChangeEvent(String topic, Long repoId, String changes) throws JsonProcessingException {
        RepoChangeEvent event = new RepoChangeEvent(
            repoId,
            changes,
            LocalDateTime.now()
        );

        // Send message to Kafka
        kafkaTemplate.send(topic, repoId.toString(), objectMapper.writeValueAsString(event))
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("{} Successfully sent change event for repository id: {}", 
                            LOG_PREFIX, repoId);
                } else {
                    log.error("{} Failed to send change event for repository id: {}", 
                            LOG_PREFIX, repoId, ex);
                }
            });
    }
} 