# GitHub Repository Analyzer

## Overview
GitHub Repository Analyzer is a Spring Boot application that helps track and analyze GitHub repositories. It provides functionality to store repository information, process repository data in batches, and retrieve repository details through a RESTful API.

## Technologies Used
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL 15
- Docker & Docker Compose
- Maven
- Swagger/OpenAPI 3.0
- Lombok
- JUnit 5
- Zipkin

## Features
- CRUD operations for GitHub repositories
- Batch processing of repository data
- Pagination and filtering support
- API versioning
- Comprehensive error handling
- Swagger documentation

## API Endpoints

### Base Path: `/api/v1/repos`

| Method | Endpoint | Description | Query Parameters |
|--------|----------|-------------|------------------|
| POST | `/` | Create or update a repository | N/A |
| GET | `/` | List all repositories | `owner`, `name`, `status`, `licence`, `page`, `size` |
| GET | `/{id}` | Get repository by ID | N/A |
| DELETE | `/{id}` | Delete repository by ID | N/A |

#### Filtering Parameters
- `owner`: Filter by repository owner (optional)
- `name`: Filter by repository name (optional)
- `status`: Filter by repository status (optional)
- `licence`: Filter by repository license (optional)
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)

## Docker Images
- PostgreSQL 15
- OpenJDK 17
- Zipkin

## Prerequisites
- Docker and Docker Compose
- Java 17 or higher
- Maven 3.8+
- Git

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/github-analyzer.git
cd github-analyzer
```

### 3. Build the Application
```bash
mvn clean package -DskipTests
```

### 4. Start the Services
```bash
docker-compose up -d
```

### 5. Access the Application
- API Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Documentation
Detailed API documentation is available through Swagger UI after starting the application.

## Database Schema
The application uses PostgreSQL with the following main table:

```sql
CREATE TABLE observed_repo (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner VARCHAR(100) NOT NULL,
    description TEXT,
    language VARCHAR(50),
    stars INTEGER,
    forks INTEGER,
    url VARCHAR(255),
    status VARCHAR(20),
    licence VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```
Initializers some indexes depending the given filters:

```sql
CREATE INDEX idx_repo_owner ON observed_repo (repo_owner);
CREATE INDEX idx_repo_licence ON observed_repo (licence);
CREATE INDEX idx_repo_status ON observed_repo (repo_status);
CREATE INDEX idx_repo_owner_name ON observed_repo (repo_name, repo_owner);
```

## Development

### Running Tests
```bash
mvn test
```

## Distributed Tracing with Zipkin

This application includes distributed tracing capabilities using Zipkin. This helps monitor and troubleshoot the application by tracking requests across different components.

### Features
- Request tracing across services
- Visualization of request flow
- Performance monitoring
- Dependency analysis

### Accessing Zipkin
The Zipkin UI is available at: http://localhost:9411

### Traced Operations
The following operations are traced:
- Repository processing (`ObservedRepoProcessingService`)
- Scheduled import operations (`ObservedRepoImportScheduler`)

### Configuration
Tracing configuration can be adjusted in `application.properties`:

## License
This project is licensed under the MIT License - see the LICENSE file for details.