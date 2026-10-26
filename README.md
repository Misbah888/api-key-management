# API Key Management & Usage Tracking System

A backend REST API built with Java and Spring Boot to manage API keys securely, authenticate developers, enforce usage limits, and track API consumption.

## Features

- **Secure API Key Generation:** Generates cryptographically secure API keys.
- **Hashed Key Storage:** Stores SHA-256 hashes instead of raw API keys.
- **JWT Authentication:** Uses JSON Web Tokens to authenticate developers and administrators.
- **Role-Based Access Control:** Separates administrator and developer permissions.
- **Developer Registration & Login:** Allows developers to register and authenticate.
- **API Key Management:** Create, list, and revoke API keys.
- **Usage Tracking:** Tracks API requests and provides usage statistics.
- **Daily Usage Limits:** Tracks requests against each API key's daily quota.
- **Rate Limiting:** Limits requests to 10 per minute per API key.
- **Ownership Validation:** Prevents developers from accessing other developers' API key statistics.
- **Input Validation & Error Handling:** Validates requests and returns structured error responses.
- **Unit Testing:** Uses JUnit 5 and Mockito to test service-layer business logic.

## Technology Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Security:** Spring Security, JWT
- **Database:** MySQL
- **Persistence:** Spring Data JPA, Hibernate
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito
- **API Testing:** Postman
- **Version Control:** Git and GitHub

## Architecture

The application follows a layered architecture:

- **Controller Layer:** Exposes REST endpoints and handles HTTP requests.
- **Service Layer:** Implements business logic for API key management and usage tracking.
- **Repository Layer:** Interacts with MySQL through Spring Data JPA.
- **Security Layer:** Handles JWT authentication, API key authentication, and rate limiting.
- **DTO Layer:** Defines request and response objects.
- **Exception Layer:** Provides centralized exception handling for applicable application errors.

## API Endpoints

### Administrator Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/keys` | Create an API key |
| GET | `/api/keys` | List API keys |
| PATCH | `/api/keys/{id}/revoke` | Revoke an API key |
| GET | `/api/keys/{id}/stats` | View API key usage statistics |

Administrator endpoints require appropriate administrator authentication.

### Developer Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/developers/register` | Register a developer |
| POST | `/api/developers/login` | Authenticate a developer |
| GET | `/api/developers/keys` | List the authenticated developer's keys |
| POST | `/api/developers/keys` | Create an API key for the authenticated developer |
| GET | `/api/developers/keys/{id}/stats` | View usage statistics for an owned API key |

Developer endpoints use JWT authentication where required.

### Protected Demo API

The `/api/demo/**` endpoints use API key authentication. Requests must include a valid API key in the `X-API-Key` HTTP header.

## Security Design

### API Key Storage

1. Generate a cryptographically secure random API key.
2. Return the raw key to the caller when it is created.
3. Calculate its SHA-256 hash.
4. Store the hash rather than the raw key in the database.
5. Use the hash to identify API keys during authentication.

**Important:** The raw API key is sensitive and should not be shared or committed to source control.

### JWT Authentication

JWTs authenticate developers and administrators. Protected endpoints enforce the permissions associated with the authenticated user.

### Rate Limiting and Usage Tracking

The application enforces a fixed-window rate limit of 10 requests per minute per API key. It also tracks API usage and checks daily request quotas.

## Database

The application uses MySQL to persist API keys and usage records.

Configure your database connection and required environment variables before starting the application.

## Configuration

Set the following environment variables:

| Variable | Purpose |
|---|---|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Base64-encoded secret used for signing JWTs |
| `APP_ADMIN_PASSWORD` | Administrator password |

Use your own local credentials. Do not commit actual passwords or production secrets to GitHub.

Generate a strong JWT signing secret and configure it as an environment variable before running the application.

## Running the Application

### Prerequisites

- Java version compatible with the project configuration
- MySQL
- Git

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/Misbah888/api-key-management.git
   ```

2. Open the project directory:

   ```bash
   cd api-key-management
   ```

3. Create the MySQL database configured in the application.

4. Configure the required environment variables.

5. Run the application on Windows:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

6. Use Postman to test the endpoints.

Check the application's configuration for the database name, server port, and any additional required environment variables.

## Running Tests

Run the automated test suite on Windows:

```powershell
.\mvnw.cmd clean test
```


## Future Improvements

- Persistent rate limiting using Redis
- Refresh-token support
- More comprehensive integration and security tests
- API documentation with OpenAPI/Swagger
- Containerization using Docker
- Monitoring and operational metrics

## Author

**Mohammed Misbah**

GitHub: [Misbah888](https://github.com/Misbah888)
