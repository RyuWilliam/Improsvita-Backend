# AGENTS.md - improsvita backend

## Project Overview
Spring Boot 4.1.1 (Java 24) + Gradle + PostgreSQL

## Key Commands
Run from `improsvita/improsvita/`:
- `./gradlew bootRun` — start app (port 19010 in dev, 9010 in prod, context `/improsvita/api`)
- `./gradlew test` — run tests (JUnit Platform)
- `./gradlew build` — build jar
- `docker compose up -d` — start PostgreSQL (port 9011)

## Profiles
- **dev** (default): local PostgreSQL via Docker Compose (`compose.yaml`), `spring.docker.compose.enabled=true` implicitly, port 9011, `ddl-auto=create-drop` (schema + `data-dev.sql` recreated on each run).
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
- Public: `GET /seeds/**`, `GET /contacts/**`, `GET /suppliers/**`, `GET /clients/**`, `/auth/**`, `/error`
- Admin-only: `POST/PUT/PATCH/DELETE` on any path requires `ROLE_ADMIN`
- JWT token via `Authorization: Bearer <token>` header
- Unauthenticated → **401** (`AuthenticationEntryPoint`); authenticated without the role → **403** (`AccessDeniedHandler`). Do not remove these or Spring falls back to `Http403ForbiddenEntryPoint` and every unauthenticated call returns 403.
- **`/error` must stay `permitAll()`.** If it is protected, the container's error dispatch for any 404/405/500 is itself rejected and the client sees an empty 403 instead of the real status.
- Error bodies are **plain text** (not JSON). `web/error/GlobalExceptionHandler` is the catch-all for anything the controllers do not catch.

## API Notes
- **`Contact` is the single person/company aggregate** (`contacts` + `contact_roles`). Roles: `SUPPLIER`, `CLIENT`, `EMPLOYEE`. There is no `Client` or `Supplier` domain/persistence class.
- `/contacts` is the canonical CRUD (`?role=` filter). `/suppliers` and `/clients` are compatibility aliases that filter by role `SUPPLIER` / `CLIENT`.
- `reservations.contact_id` → `contacts.contact_id`; reservations require the contact to have the `CLIENT` role.
- Migrations: `docs/migrations/contacts-migration.sql` (suppliers → contacts) and then `docs/migrations/clients-to-contacts.sql` (clients → contacts + reservations FK). Run against prod before deploying.
- `Visits` hang off `Sowing` (1:N, `mappedBy`, no cascade) and are exposed only as `/sowings/{sowingId}/visits`.
- **`visits` es la única tabla de seguimiento de la plantación** (no existe `sowing_reviews`). `suitable_estimated` = plántulas aptas y es la fuente de disponibilidad para vender: se toma la **última visita activa** de la siembra.
- Reglas de `VisitService`: `suitableEstimated` obligatorio y `>= 0`, **no mayor que `sowing.quantitySown`**; `visitDate` no futura ni anterior a la fecha de siembra.
- `SowingResponse` expone `germinatedQuantity` (última visita activa), `soldQuantity` (ventas activas) y `seedlingsAvailable` = germinadas - vendidas (nunca negativo; 0 si no hay visitas).
- `Reservations` link `Sowing` + `Contact` and live at top level: `/reservations` with filters `?sowingId=`, `?clientId=`, `?status=`. Reads require a token (not a public catalog).
- **One live reservation per sowing**: a sowing is blocked while it has a reservation that is neither `CANCELLED` nor inactive. Cancelling frees it. A live reservation is only allowed on `PLANNED`/`IN_PROGRESS` sowings.
- `reservations.total_price` is `BigDecimal(12,2)` — the only monetary field in the model.
- **`sales`** registra las ventas de plántulas. `sowing_id` es obligatorio (el stock está en la siembra); `reservation_id` es `null` en venta directa y apunta a la reserva cuando la venta es su entrega. `sale_type` = `DIRECT`/`RESERVATION`. El total lo calcula el servidor (`quantity * unit_price`).
- Disponibilidad vendible = `suitable_estimated` de la **última visita activa** − `SUM(sales.quantity)` con `active = true`. Nunca es negativa; sin visitas es 0.
- Reglas de `SaleService`: comprador con rol `CLIENT`; siembra activa en `PLANNED`/`IN_PROGRESS`/`COMPLETED`; **una reserva viva bloquea la venta directa**; una reserva se factura como máximo una vez; las ventas bloquean la fila de la siembra para evitar sobreventa concurrente; `DELETE /sales/{id}` es anulación lógica (`active = false`).
- Endpoints: `POST /sales`, `POST /sales/reservation`, `GET /sales`, `GET /sales/page?page=&size=&contactId=&type=&from=&to=`, `GET /sales/{id}`, `GET /sales/availability/{sowingId}`, `DELETE /sales/{id}`. Tamaño de página máximo: 100.
- Every inventory movement is traceable: `seed_movement.sowing_id` links the EXIT created when sowing, so deleting a sowing can reverse the exact amount consumed.
- `DELETE /sowings/{id}` returns the consumed stock to the lot (compensating `ADJUSTMENT`) and refuses with 409 if the sowing has visits **or reservations**.
- All quantities are `Integer`; seed `totalAvailable` is a SQL `SUM` exposed as `Long`.

## Gotchas
- Java 24 toolchain required (see `build.gradle:11-14`)
- Use Gradle wrapper (`./gradlew`), not system Gradle
- Context path is `/improsvita/api` — all URLs include it (e.g. `http://localhost:19010/improsvita/api/auth/login` in dev)
- Controllers use paths WITHOUT `/api` prefix (e.g. `@RequestMapping("/seeds")`) since context path already includes it