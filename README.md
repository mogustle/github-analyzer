# GitHub Repository Analyzer

A distributed system for analyzing GitHub repositories, consisting of a publisher and consumer service.

## Architecture Overview of Kafka message flow

### Message Flow
1. Publisher detects repository changes
2. Changes are sent to Kafka topic 'repo-changes'
3. Consumer processes changes and updates analysis

## Services

### Publisher
- Fetches repository data from GitHub API
- Detects and publishes repository changes
- Maintains repository data
- Implements rate limiting

### Consumer
- Consumes repository change events
- Logs change events

### Kafka
- Message broker for change events
- Ensures reliable message delivery
- Enables service decoupling
- Supports scalable processing

### Message Format

```json
{
    "repoId": 123,
    "changes": "Changes description",
    "timestamp": "2024-03-21T10:15:30"
}
```

## Quick Start

### 1. Start the services
```bash
docker compose up -d
```

### 2. Run the publisher:
```bash
mvn spring-boot:run -pl publisher
```

### 3. Run the consumer:
```bash
mvn spring-boot:run -pl consumer
```


