# AGENTS.md - improsvita backend

## Project Overview
Spring Boot 4.1.1 (Java 24) + Gradle + PostgreSQL

## Key Commands
Run from `improsvita/improsvita/`:
- `./gradlew bootRun` — start app (port 9010, context `/improsvita/api`)
- `./gradlew test` — run tests (JUnit Platform)
- `./gradlew build` — build jar
- `docker compose up -d` — start PostgreSQL (port 9011)

## Profiles
- **dev** (default): local PostgreSQL via Docker Compose (`compose.yaml`), `spring.docker.compose.enabled=true` implicitly, port 9011, `ddl-auto=update`.
- **prod**: Railway PostgreSQL, `spring.docker.compose.enabled=false`, requires env vars (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`).

To run against Railway:
```powershell
$env:SPRING_PROFILES_ACTIVE="prod"; ./gradlew bootRun
```

## Docker Compose
- `compose.yaml` at `improsvita/improsvita/compose.yaml` — local PostgreSQL only (port 9011).
- **Does NOT start automatically in prod** — controlled by `spring.docker.compose.enabled` in each profile.
- `spring-boot-docker-compose` is a `developmentOnly` dependency (`build.gradle:26`), so it only activates when `dev` profile is active.

## Configuration
- `application.properties`: base config, `spring.profiles.active=dev`
- `application-dev.properties`: local DB `jdbc:postgresql://localhost:9011/local_database` (user: william, pass: secret123)
- `application-prod.properties`: Railway DB, secrets via env vars with fallback defaults

## Notable Dependencies
- `spring-boot-starter-data-jpa`, `spring-boot-starter-webmvc`
- `spring-boot-starter-security` — JWT auth with `JwtAuthenticationFilter`
- `springdoc-openapi-starter-webmvc-ui:3.1.0` — Swagger at `/improsvita/api/swagger-ui.html`
- `org.mapstruct:mapstruct:1.6.3` — annotation processor configured
- `io.jsonwebtoken:jjwt-*` — JWT support
- CORS enabled via `CorsConfigurationSource` bean in `SecurityConfig`

## Security
- `SecurityFilterChain`: CORS enabled, CSRF disabled, stateless sessions
- Public: `GET /seeds/**`, `GET /suppliers/**`, `GET /clients/**`, `/auth/**`, `/error`
- Admin-only: `POST/PUT/PATCH/DELETE` on any path requires `ROLE_ADMIN`
- JWT token via `Authorization: Bearer <token>` header
- Unauthenticated → **401** (`AuthenticationEntryPoint`); authenticated without the role → **403** (`AccessDeniedHandler`). Do not remove these or Spring falls back to `Http403ForbiddenEntryPoint` and every unauthenticated call returns 403.
- **`/error` must stay `permitAll()`.** If it is protected, the container's error dispatch for any 404/405/500 is itself rejected and the client sees an empty 403 instead of the real status.
- Error bodies are **plain text** (not JSON). `web/error/GlobalExceptionHandler` is the catch-all for anything the controllers do not catch.

## API Notes
- `ClientEntity` mirrors `SupplierEntity` but is unconnected to any other table.
- `Visits` hang off `Sowing` (1:N, `mappedBy`, no cascade) and are exposed only as `/sowings/{sowingId}/visits`.
- Every inventory movement is traceable: `seed_movement.sowing_id` links the EXIT created when sowing, so deleting a sowing can reverse the exact amount consumed.
- `DELETE /sowings/{id}` returns the consumed stock to the lot (compensating `ADJUSTMENT`) and refuses with 409 if the sowing has visits.
- All quantities are `Integer`; seed `totalAvailable` is a SQL `SUM` exposed as `Long`.

## Gotchas
- Java 24 toolchain required (see `build.gradle:11-14`)
- Use Gradle wrapper (`./gradlew`), not system Gradle
- Context path is `/improsvita/api` — all URLs include it (e.g. `http://localhost:9010/improsvita/api/auth/login`)
- Controllers use paths WITHOUT `/api` prefix (e.g. `@RequestMapping("/seeds")`) since context path already includes it