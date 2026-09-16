# AGENTS.md - improsvita backend

## Project Overview
Spring Boot 4.1.1 (Java 24) + Gradle + PostgreSQL

## Key Commands
Run from `improsvita/improsvita/`:
- `./gradlew bootRun` — start app (port 9010, context `/improsvita/api`)
- `./gradlew test` — run tests (JUnit Platform)
- `./gradlew build` — build jar
- `docker compose up -d` — start PostgreSQL (port 9011)

## Configuration
- `application-dev.properties` active by default (`spring.profiles.active=dev`)
- DB: `jdbc:postgresql://localhost:9011/local_database` (user: william, pass: secret123)
- `spring.jpa.hibernate.ddl-auto=create-drop` (dev only)

## Notable Dependencies
- `spring-boot-starter-data-jpa`, `spring-boot-starter-webmvc`
- `springdoc-openapi-starter-webmvc-ui:3.1.0` — Swagger at `/improsvita/api/swagger-ui.html`
- `org.mapstruct:mapstruct:1.6.3` — annotation processor configured
- `io.jsonwebtoken:jjwt-*` — JWT support
- Security starter **commented out** in build.gradle

## Gotchas
- Java 24 toolchain required (see `build.gradle:11-14`)
- Use Gradle wrapper (`./gradlew`), not system Gradle
- Docker compose file at `improsvita/improsvita/compose.yaml`
- No existing AGENTS.md, CLAUDE.md, or copilot instructions