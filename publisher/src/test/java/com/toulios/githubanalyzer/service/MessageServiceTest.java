package com.toulios.githubanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    private static final String TOPIC = "test-topic";
    private static final Long REPO_ID = 123L;
    private static final String CHANGES = "test changes";

    private KafkaTemplate<String, String> kafkaTemplate;

    private MessageService messageService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        kafkaTemplate = (KafkaTemplate<String, String>) mock(KafkaTemplate.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonNullableModule());
        objectMapper.registerModule(new JavaTimeModule());

        messageService = new MessageService(objectMapper, kafkaTemplate);
    }

    @Test
    void sendChangeEvent_WhenSuccessful_ShouldSendMessageToKafka() throws JsonProcessingException {
        // Arrange
        SendResult<String, String> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        // Act
        messageService.sendChangeEvent(TOPIC, REPO_ID, CHANGES);

        // Assert
        verify(kafkaTemplate).send(eq(TOPIC), eq(REPO_ID.toString()), anyString());
    }

    @Test
    void sendChangeEvent_WhenFailed_ShouldHandleError() throws JsonProcessingException {
        // Arrange
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Failed to send message"));
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        // Act
        messageService.sendChangeEvent(TOPIC, REPO_ID, CHANGES);

        // Assert
        verify(kafkaTemplate).send(eq(TOPIC), eq(REPO_ID.toString()), anyString());
    }
} 