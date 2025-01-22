# GitHub Repository Analyzer

## Overview
GitHub Repository Analyzer is a Spring Boot application that helps track and analyze GitHub repositories. It provides functionality to store repository information, process repository data in batches, and retrieve repository details through a RESTful API.

## Technical Stack

| Category | Technologies |
|----------|--------------|
| Core | Java 17, Spring Boot 3.x |
| Database | PostgreSQL 15, Spring Data JPA, Flyway |
| Documentation | OpenAPI/Swagger 3.0 |
| Monitoring | Zipkin |
| Tools | Docker, Maven, Lombok |
| Testing | JUnit 5 |
| Rate Limiting | Resilience4j |

## Features
- Fetch and analyze GitHub repository details
- Batch processing of repository data
- Pagination and filtering support
- API versioning
- Comprehensive error handling
- OpenAPI/Swagger documentation
- Distributed tracing with Zipkin
- Smart rate limiting to respect GitHub API quotas (5000 requests/hour)
- Configurable buffer zone to prevent hitting rate limits
- Detailed logging with component-specific prefixes
- PostgreSQL database integration
- Flyway database migrations

### Rate Limiting

The application implements smart rate limiting to prevent exceeding GitHub's API limits:
- Tracks remaining API requests
- Maintains a configurable buffer (default 1% of total limit)
- Automatically pauses when approaching limits
- Resumes when rate limits reset

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

## Access Points

| Service | URL | Description |
|---------|-----|-------------|
| API Base | http://localhost:8080 | Main application |
| Swagger UI | http://localhost:8080/swagger-ui.html | API documentation |
| Zipkin | http://localhost:9411 | Distributed tracing |

## Database Indexes

| Table | Indexes |
|-------|---------|
| observed_repo | • idx_repo_owner<br>• idx_repo_licence<br>• idx_repo_status<br>• idx_repo_owner_name |

## Quick Start

```bash
# Clone repository
git clone https://github.com/mogustle/github-analyzer.git
cd github-analyzer

# Build application
mvn clean package -DskipTests

# Start services
docker-compose up -d

# Run application
mvn spring-boot:run
```