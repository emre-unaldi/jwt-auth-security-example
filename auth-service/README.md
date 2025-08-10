# Authentication Service

JWT-based authentication and authorization microservice.

## Features

* JWT token-based authentication
* Access Token and Refresh Token mechanism
* Dynamic role and privilege management
* Token blacklist management with Redis
* Data integrity with soft delete
* Comprehensive audit trail
* Swagger/OpenAPI documentation
* Advanced logging with Log4j2

## Technologies

* Java 21 LTS
* Spring Boot 3.3.5
* Spring Security
* Spring Data JPA
* Spring Cloud OpenFeign
* PostgreSQL
* Redis
* MapStruct
* Lombok
* JWT (jjwt)
* Swagger/OpenAPI 3

## Getting Started

### Prerequisites

* Java 21
* Maven 3.8+
* Docker & Docker Compose

### Installation

1. Start Docker containers:

```bash
docker-compose up -d
```

2. Build the service:

```bash
mvn clean install
```

3. Run the service:

```bash
mvn spring-boot:run
```

## API Endpoints

### Authentication Endpoints

* `POST /api/v1/auth/login` - User login
* `POST /api/v1/auth/refresh` - Token refresh
* `POST /api/v1/auth/validate` - Token validation
* `POST /api/v1/auth/logout` - Logout

### Role Management Endpoints

* `GET /api/v1/roles` - List all roles
* `GET /api/v1/roles/{id}` - Get role details
* `POST /api/v1/roles` - Create new role
* `PUT /api/v1/roles/{id}` - Update role
* `DELETE /api/v1/roles/{id}` - Delete role
* `POST /api/v1/roles/assign-privileges` - Assign privileges to role
* `POST /api/v1/roles/remove-privileges` - Remove privileges from role

### Privilege Management Endpoints

* `GET /api/v1/privileges` - List all privileges
* `GET /api/v1/privileges/{id}` - Get privilege details
* `POST /api/v1/privileges` - Create new privilege
* `PUT /api/v1/privileges/{id}` - Update privilege
* `DELETE /api/v1/privileges/{id}` - Delete privilege

## Swagger Documentation

After running the service, you can access Swagger UI at:

```
http://localhost:8081/swagger-ui.html
```

## Security

### JWT Token Structure

Access Token contents:
* userId: User ID
* email: User email
* roles: List of roles
* privileges: List of privileges
* tokenType: Token type (ACCESS)

### Token Expiration Times

* Access Token: 15 minutes
* Refresh Token: 7 days

## Database Schema

### Tables

* `roles` - System roles
* `privileges` - System privileges
* `role_privileges` - Role-privilege relationships
* `refresh_tokens` - Refresh token records

## Monitoring

### Health Check

```
GET http://localhost:8081/actuator/health
```

### Metrics

```
GET http://localhost:8081/actuator/metrics
```

### Log Files

* `logs/auth-service.log` - General application logs
* `logs/auth-security.log` - Security logs
* `logs/auth-error.log` - Error logs