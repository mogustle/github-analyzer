package com.toulios.consumer.listener;

import com.toulios.consumer.config.KafkaTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
@DirtiesContext
@Import(KafkaTestConfig.class)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class RepoChangeListenerIntegrationTest {

    private static final String TOPIC = "repo-changes";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        // Clear any messages from previous tests
    }

    @Test
    void whenSendingMessage_thenMessageIsReceived() {
        // Given


        // When
        kafkaTemplate.send(TOPIC, "{}");

        // Then
        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    
                });
    }
} 