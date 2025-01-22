package com.toulios.consumer.config;

import com.toulios.consumer.event.RepoChangeEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaTestConfig is a configuration class for Kafka tests.
 * It provides a producer factory and a Kafka template for sending messages to Kafka.
 */
@TestConfiguration
public class KafkaTestConfig {
    
    /**
     * ProducerFactory is a factory for creating Kafka producer instances.
     * It provides a map of configuration properties for the producer.
     * @return a new instance of DefaultKafkaProducerFactory
     */
    @Bean
    public ProducerFactory<String, RepoChangeEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * KafkaTemplate is a template for sending messages to Kafka.
     * It provides a method to send messages to a specific topic.
     * @return a new instance of KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, RepoChangeEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * ConsumerFactory is a factory for creating Kafka consumer instances.
     * It provides a map of configuration properties for the consumer.
     * @return a new instance of DefaultKafkaConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, RepoChangeEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * ConcurrentKafkaListenerContainerFactory is a factory for creating Kafka listener container factories.
     * It provides a method to create a listener container factory for a specific topic.
     * @return a new instance of ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RepoChangeEvent> 
            kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RepoChangeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
} 