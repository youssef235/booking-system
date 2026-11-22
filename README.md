# Booking Management System

A REST API and web interface for managing service providers, services, availability schedules, and appointment bookings, built with Spring Boot and Clean Architecture.

## Tech Stack

- **Backend:** Java 21, Spring Boot 4, Spring Security (Stateless JWT / HS256), Spring Data JPA
- **Database:** PostgreSQL 17, Flyway migrations
- **API Docs:** OpenAPI 3 / Swagger UI
- **Frontend:** Vanilla HTML, CSS, and JavaScript served from `src/main/resources/static`
- **Infrastructure:** Docker Compose, Maven Wrapper

## Architecture

The project follows **Clean Architecture** organized by feature (`user`, `provider`, `service`, `availability`, `booking`, `shared`, `config`):

- `domain`: Pure Java entities, value objects, domain rules, exceptions, and repository interfaces (no framework dependencies).
- `application`: Pure Java use cases, commands, and results wired explicitly via `@Bean` in `UseCaseConfig`.
- `infrastructure`: JPA entities, mappers, Spring Data repositories, adapters, BCrypt hashing, and JWT token issuance.
- `presentation`: REST controllers, request/response DTOs, validation, and centralized error handling (`GlobalExceptionHandler`).

Double-booking prevention is enforced at both the application layer and the PostgreSQL database level using an exclusion constraint (`ex_bookings_no_overlap`).

## Getting Started

### Prerequisites

- Java 21
- Docker Desktop (for PostgreSQL)

### 1. Configure Environment Variables

Copy `.env.example` to `.env` and set your local values (`JWT_SECRET` must be at least 32 characters):

```bash
cp .env.example .env
```

### 2. Start PostgreSQL

```bash
docker compose up -d
```

### 3. Run the Application

On Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS / Linux:

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8081`:
- **Web UI:** `http://localhost:8081`
- **Swagger UI:** `http://localhost:8081/swagger-ui.html`

## Running Tests

Make sure PostgreSQL is running (`docker compose up -d`), then run:

```powershell
.\mvnw.cmd test
```