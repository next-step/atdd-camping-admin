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

## Feature File Writing Guidelines

Feature files describe business behavior at a **high level**. Step text should be readable by non-technical stakeholders.

**Do:**
- Write step text in natural Korean that describes intent, not implementation
- Put the API endpoint in commend in steps: `# GET "/admin/products"`
- Describe outcomes in business terms: `And 상품 재고가 감소한다`

**Don't:**
- Embed data tables in feature steps — put test data in step definitions instead
- Assert specific field values in the feature file: ~~`And 응답의 "name" 값은 "모기향"이다`~~
- Use verbose technical phrasing: ~~`When 다음 정보로 상품 등록 요청을 보내면`~~
- Include query parameters or request details in the URL: ~~`(GET "/admin/reports/revenue/daily?date={today}")`~~
- No put HTTP status code in parentheses at the end of `Then` steps: `Then 조회에 성공한다`

**Example (good):**
```gherkin
Scenario: 판매 상품을 등록한다
  When 판매 상품을 등록한다 (POST "/admin/products")
  Then 상품이 생성된다 (201)
  And 생성된 상품 정보가 반환된다
```

**Example (bad):**
```gherkin
Scenario: 판매 상품을 등록한다
  When 다음 정보로 상품 등록 요청을 보내면 (POST "/admin/products")
    | name | stockQuantity | price | productType |
    | 모기향  | 100           | 3000  | SALE        |
  Then 리소스가 생성된다 (201)
  And 응답의 "name" 값은 "모기향"이다
  And 응답의 "stockQuantity" 값은 100이다
```

## Test Architecture

Tests use Cucumber + REST Assured against a real Spring Boot server started on a random port.

**Key files:**
- `CucumberSpringConfiguration` — `@SpringBootTest(RANDOM_PORT)` + `@CucumberContextConfiguration`, sets `RestAssured.port`
- `support/TestContext` — `@Scope("cucumber-glue")` shared state per scenario (response, JWT token, entity IDs, stock snapshots)
- `steps/HookSteps` — `@Before` hook: deletes all data in FK-safe order, then logs in to obtain JWT token
- `steps/CommonSteps` — steps shared across features: `캠프사이트가 등록되어 있다`, `확정된 예약이 존재한다`, `조회에 성공한다`, `수정에 성공한다`, `상품 재고가 감소한다/복구된다`
- `src/test/resources/application.yml` — overrides prod config: H2 in-memory, `sql.init.mode: never` (no data.sql)

**DB cleanup order** (FK constraints):
`rental_records` → `sales_records` → `reservations` → `campsites` → `products`

**Auth:** every test scenario logs in via `POST /auth/login` in `@Before` and stores the JWT in `TestContext.jwtToken`. All API calls use `context.authRequest()` which adds the `Authorization: Bearer` header.

**Step definition conventions:**
- Steps with regex special chars in URL (parentheses, braces) use regex patterns starting with `^`
- Given steps use repositories to insert data directly (faster, no auth loop)
- When steps call the API via REST Assured and store the response in `context.response`
- Then/And steps assert against `context.response` or query repositories directly

## Known Intentional Issues

- `CampsiteAdminController.updateCampsite` and `ProductAdminController.updateProduct` do not call `repository.save()`
- `Reservation.status` is a raw `String` despite `ReservationStatus` enum existing
- Revenue reporting uses `findAll()` + Java stream filtering instead of database queries
