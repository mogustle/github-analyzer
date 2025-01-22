package com.toulios.githubanalyzer.service;

import com.toulios.githubanalyzer.event.RepoChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageServiceTest {

    private static final String TOPIC = "test-topic";
    private static final Long REPO_ID = 123L;
    private static final String CHANGES = "test changes";

    private KafkaTemplate<String, RepoChangeEvent> kafkaTemplate;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = (KafkaTemplate<String, RepoChangeEvent>) mock(KafkaTemplate.class);
        messageService = new MessageService(kafkaTemplate);
    }

    @Test
    void sendChangeEvent_WhenSuccessful_ShouldSendMessageToKafka() {
        // Arrange
        SendResult<String, RepoChangeEvent> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, RepoChangeEvent>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        // Act
        messageService.sendChangeEvent(TOPIC, REPO_ID, CHANGES);

        // Assert
        verify(kafkaTemplate).send(eq(TOPIC), eq(REPO_ID.toString()), any(RepoChangeEvent.class));
    }

    @Test
    void sendChangeEvent_WhenFailed_ShouldHandleError() {
        // Arrange
        CompletableFuture<SendResult<String, RepoChangeEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Failed to send message"));
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        // Act
        messageService.sendChangeEvent(TOPIC, REPO_ID, CHANGES);

        // Assert
        verify(kafkaTemplate).send(eq(TOPIC), eq(REPO_ID.toString()), any(RepoChangeEvent.class));
    }
} 