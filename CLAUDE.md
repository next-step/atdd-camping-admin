# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot camping ground admin system (초록 캠핑장 관리자 시스템) used as a **learning project for ATDD (Acceptance Test-Driven Development)**. The codebase contains intentional legacy code smells (duplicated logic, direct repository access from controllers, missing `.save()` calls) meant to be discovered and fixed through test-driven development.

## Build & Run Commands

```bash
./gradlew build          # Build the project
./gradlew bootRun        # Run the application (http://localhost:8080)
./gradlew test           # Run all tests
```

## Tech Stack

- **Java 17** / **Spring Boot 3.2.0** / **Gradle 8.14**
- **H2 in-memory database** (reset on each restart, seeded via `data.sql`)
- **Thymeleaf** for server-rendered HTML views
- **Custom JWT auth** (no Spring Security) — hardcoded admin credentials in `application.yml`
- **Testing:** Cucumber 7.14.0, REST Assured 5.3.2, JUnit Platform Suite

## Architecture

The application has a **dual-layer controller architecture**:

1. **REST API controllers** (`controller/` package) — `@RestController` serving JSON at `/admin/**` and `/api/**`
2. **Web MVC controllers** (`web/` package) — `@Controller` serving Thymeleaf HTML at `/console/**`

Some web controllers bypass the service layer and call repositories directly (intentional legacy pattern).

### Key packages under `com.camping.admin`:
- `config/` — WebConfig with JWT filter registration
- `controller/` — REST API endpoints
- `web/` — Thymeleaf page controllers
- `service/` — ProductService, RentalService, SalesService
- `domain/entity/` — JPA entities (Campsite, Customer, Product, RentalRecord, Reservation, SalesRecord)
- `domain/enums/` — CampsiteStatus, ProductType, ReservationStatus
- `dto/` — Request/Response DTOs
- `repository/` — Spring Data JPA repositories
- `security/` — JwtAuthFilter, JwtService

## Testing Setup

Test dependencies (Cucumber, REST Assured) are configured in `build.gradle` but **no tests exist yet** — they are to be written as ATDD exercises. Expected structure:
- Feature files (`.feature`) in `src/test/resources/`
- Step definitions in `src/test/java/`
- JUnit Platform Suite runner class

## Code Rules

- **Gherkin keywords must be English** — use `Given`, `When`, `Then`, `And`, `But` in `.feature` files. Descriptions and step text should remain in Korean.
- **Step definition annotations must use English** — use `io.cucumber.java.en.*` imports (`@Given`, `@When`, `@Then`, `@And`), never Korean (`io.cucumber.java.ko.*`)

## Known Intentional Issues

- `CampsiteAdminController.updateCampsite` and `ProductAdminController.updateProduct` do not call `repository.save()`
- `Reservation.status` is a raw `String` despite `ReservationStatus` enum existing
- Revenue reporting uses `findAll()` + Java stream filtering instead of database queries
