# GitHub Repository Analyzer - Consumer Service

Consumer service for the GitHub Repository Analyzer system. This service processes repository change events from Kafka and provides analysis capabilities.

## Features

- Kafka message consumption
- Repository change event processing
- Logging of repository changes

## Technical Stack

| Category | Technologies |
|----------|--------------|
| Core | Java 17, Spring Boot 3.x |
| Messaging | Apache Kafka |

## Message Format

The service consumes messages in the following format:
```json
{
    "repoId": "123",
    "changes": "Repository changes description",
    "timestamp": "2024-03-21T10:15:30"
}
```

## Configuration

### Kafka Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: analysis-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.toulios.consumer.event"
```

## Quick Start

1. Configure environment
```bash
cp .env.example .env.local
# Edit .env.local with your values
source .env.local
```

2. Start required services
```bash
docker-compose up -d
```