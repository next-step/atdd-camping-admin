# 인수 테스트 전략 (Acceptance Testing Strategy)

이 문서는 캠핑장 관리 서비스의 인수 테스트(ATDD) 전략을 정의합니다.

---

## 1. 핵심 기술 스택

| 구분 | 기술 | 버전 | 용도 |
|------|------|------|------|
| **테스트 프레임워크** | JUnit 5 | 1.10.0 | 테스트 실행 플랫폼 |
| **BDD 프레임워크** | Cucumber | 7.14.0 | Gherkin 시나리오 기반 테스트 |
| **API 테스트** | RestAssured | 5.3.2 | HTTP API 호출 및 검증 |
| **Assertion** | AssertJ | (Spring Boot 내장) | 가독성 높은 검증문 작성 |
| **Spring 통합** | cucumber-spring | 7.14.0 | Cucumber-Spring Context 연동 |
| **데이터베이스** | H2 | (Spring Boot 내장) | 인메모리 테스트 DB |

### 의존성 설정 (build.gradle)

```gradle
ext {
    set('cucumberVersion', '7.14.0')
    set('restAssuredVersion', '5.3.2')
}

dependencies {
    // JUnit Platform Suite for Cucumber
    testImplementation 'org.junit.platform:junit-platform-suite:1.10.0'

    // Cucumber
    testImplementation "io.cucumber:cucumber-java:${cucumberVersion}"
    testImplementation "io.cucumber:cucumber-junit-platform-engine:${cucumberVersion}"
    testImplementation "io.cucumber:cucumber-spring:${cucumberVersion}"

    // RestAssured
    testImplementation "io.rest-assured:rest-assured:${restAssuredVersion}"
    testImplementation "io.rest-assured:spring-mock-mvc:${restAssuredVersion}"
}
```

---

## 2. 파일 위치 규칙

```
src/test/
├── java/com/camping/admin/
│   ├── CucumberTestRunner.java              # Cucumber 실행 진입점
│   ├── CucumberSpringConfiguration.java     # Spring Context 설정
│   ├── common/                              # 공통 설정 패키지
│   │   └── CommonHooks.java                 # 공통 Hooks, ParameterType, 공통 Step
│   ├── steps/                               # Step 정의 패키지
│   │   └── {Domain}Steps.java               # 도메인별 Step 정의
│   ├── helper/                              # 테스트 헬퍼 패키지
│   │   └── {Domain}TestHelper.java          # 도메인별 헬퍼 클래스
│   └── apiExtractableresponse/              # API 호출 유틸리티 패키지
│       └── {Domain}ApiExtractableResponse.java
└── resources/
    └── features/                            # Gherkin Feature 파일
        └── {domain}.feature
```

### 현재 도메인별 파일 목록

| 도메인 | Steps | Helper | API Response | Feature |
|--------|-------|--------|--------------|---------|
| **Campsite** | `CampsiteSteps.java` | `CampsiteTestHelper.java` | `CampsiteApiExtractableResponse.java` | `campsite.feature` |
| **Product** | `ProductSteps.java` | `ProductTestHelper.java` | `ProductApiExtractableResponse.java` | `product.feature` |
| **Rental** | `RentalSteps.java` | `RentalTestHelper.java` | `RentalApiExtractableResponse.java` | `rental.feature` |
| **Reservation** | `ReservationSteps.java` | `ReservationTestHelper.java` | `ReservationApiExtractableResponse.java` | `reservation.feature` |

### 파일별 역할

| 파일 | 역할 |
|------|------|
| `CucumberTestRunner.java` | Cucumber 테스트 Suite 실행 진입점 |
| `CucumberSpringConfiguration.java` | `@CucumberContextConfiguration` + `@SpringBootTest` 설정 |
| `CommonHooks.java` | `@Before` 훅 (DB 초기화, RestAssured 설정, 토큰 생성), `@ParameterType` 정의, 공통 `@Given`/`@Then` 스텝 |
| `{Domain}Steps.java` | Gherkin ↔ Helper 연결, 비즈니스 로직 없음 |
| `{Domain}TestHelper.java` | 테스트 상태 관리, Repository 접근, API 호출, 검증 로직 |
| `{Domain}ApiExtractableResponse.java` | RestAssured 기반 API 호출 메서드 (static) |
| `{domain}.feature` | Gherkin 시나리오 정의 |

---

## 3. 코딩 컨벤션

### 3.1 클래스 네이밍

| 유형 | 네이밍 규칙 | 예시 |
|------|-------------|------|
| Step 정의 클래스 | `{Domain}Steps` | `ReservationSteps`, `RentalSteps` |
| 헬퍼 클래스 | `{Domain}TestHelper` | `ReservationTestHelper`, `RentalTestHelper` |
| API 호출 클래스 | `{Domain}ApiExtractableResponse` | `ReservationApiExtractableResponse` |
| Feature 파일 | `{domain}.feature` (소문자) | `reservation.feature`, `rental.feature` |

### 3.2 메서드 네이밍

**한글 메서드명을 사용하여 가독성을 극대화합니다.**

| 유형 | 네이밍 패턴 | 예시 |
|------|-------------|------|
| Step 정의 | Gherkin 문장을 한글 메서드명으로 변환 | `관리자가_확정된_예약을_상태로_변경한다()` |
| Helper - 설정 | `{대상}을_생성한다` / `{대상}을_등록한다` | `대여용_상품을_생성한다()`, `사이트번호로_캠프사이트를_생성한다()` |
| Helper - 조회 | `{대상}을_찾는다` / `{대상}을_조회한다` | `확정된_예약을_찾는다()`, `첫번째_예약을_조회한다()` |
| Helper - API 호출 | `{행위}를_요청한다` / `{행위}한다` | `고객명으로_예약을_요청한다()`, `현재_예약의_상태를_변경한다()` |
| Helper - 검증 | `{조건}를_검증한다` / `{조건}인지_검증한다` | `예약_상태를_검증한다()`, `캠프사이트가_등록되었는지_검증한다()` |
| API Response | `{행위}한다` | `예약의_상태를_변경한다()`, `캠프사이트를_등록한다()` |

### 3.3 내부 주석 스타일

섹션 구분을 위해 다음 형식의 주석을 사용합니다:

```java
// ==================== 섹션명 ====================
```

**표준 섹션 구분:**

```java
// Steps 클래스
// ==================== Given ====================
// ==================== When ====================
// ==================== When - {하위 기능명} ====================   // 기능이 여러 개일 때
// ==================== Then ====================
// ==================== Then - {하위 기능명} ====================   // 기능이 여러 개일 때

// Helper 클래스
// ==================== {도메인} 설정 (Given) ====================
// ==================== {도메인} 조회 (Given/When) ====================
// ==================== API 호출 (When) ====================
// ==================== API 호출 - {하위 기능명} (When) ====================  // 기능이 여러 개일 때
// ==================== 검증 (Then) ====================
// ==================== 검증 - {하위 기능명} (Then) ====================  // 기능이 여러 개일 때
// ==================== Private ====================

// CommonHooks
// ==================== 공통 Then ====================
// ==================== Parameter Types ====================
```

### 3.4 Feature 파일 작성 규칙

**단일 기능 Feature:**

```gherkin
Feature: {기능명}
  {기능 설명}

  Background: 관리자가 로그인되어 있다

  # ==================== 정상 케이스 ====================

  Scenario: {시나리오명}
    When {행위}
    Then {결과}

  # ==================== 예외 케이스 ====================

  Scenario: {예외 시나리오명}
    When {행위}
    Then {에러 결과}

  # ==================== 엣지 케이스 ====================

  Scenario: {엣지 시나리오명}
    When {특수 행위}
    Then {결과}

# ============================================================
```

**복합 기능 Feature (하나의 도메인에 여러 기능이 있을 때):**

```gherkin
Feature: {도메인명} 관리
  {도메인 설명}

  Background: 관리자가 로그인되어 있다

  # ============================================================
  # {기능 1}
  # ============================================================

  # ==================== 정상 케이스 ====================
  Scenario: ...

  # ==================== 예외 케이스 ====================
  Scenario: ...

  # ============================================================
  # {기능 2}
  # ============================================================

  # ==================== 정상 케이스 ====================
  Scenario: ...

  # ==================== 예외 케이스 ====================
  Scenario: ...

# ============================================================
```

### 3.5 ParameterType 활용

한글 표현을 영문 코드로 매핑하기 위해 `@ParameterType`을 사용합니다:

```java
@ParameterType("취소|확정|대기|유효하지 않음")
public String 예약상태(String status) {
    ReservationStatus found = ReservationStatus.fromDisplayName(status);
    return found != null ? found.name() : status;
}
```

Feature 파일에서 사용:
```gherkin
When 관리자가 확정된 예약을 취소 상태로 변경한다
Then 예약은 확정 상태로 유지된다
```

---

## 4. 아키텍처 원칙

### 4.1 계층 분리

```
┌─────────────────────────────────────────────────────────────┐
│                    Feature 파일 (.feature)                   │
│              (비즈니스 언어로 시나리오 정의)                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Steps 클래스 (*Steps.java)                │
│         (Gherkin ↔ Helper 연결, 비즈니스 로직 없음)            │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  Helper 클래스 (*TestHelper.java)            │
│  (테스트 상태 관리, Repository 접근, API 호출 위임, 검증 수행)   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│           API Response 클래스 (*ApiExtractableResponse.java) │
│              (RestAssured 기반 HTTP 요청 수행)                │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 핵심 원칙

1. **Steps는 얇게 유지**: Step 정의 클래스는 Gherkin과 Helper를 연결하는 역할만 수행
2. **Helper에 로직 집중**: 테스트 상태 관리, Repository 접근, API 호출 위임, 검증 로직은 Helper에서 수행
3. **API 호출 분리**: HTTP 요청은 ApiExtractableResponse 클래스에서 static 메서드로 제공
4. **공통 설정은 Hooks에서**: DB 초기화, RestAssured 설정, 인증 토큰, 공통 Step은 CommonHooks에서 관리
5. **응답 공유는 CommonHooks를 통해**: `CommonHooks.lastResponse`를 통해 모든 Helper가 API 응답을 공유
6. **Helper 간 협력 허용**: 도메인 간 의존이 필요하면 Helper가 다른 Helper를 `@Autowired`로 주입받아 사용

### 4.3 테스트 데이터 초기화 전략

매 시나리오 실행 전에 `CommonHooks`의 `@Before(order = 0)`에서 다음을 수행합니다:

1. 모든 테이블을 `TRUNCATE`하여 초기화
2. `data.sql` 시드 데이터를 재삽입

이를 통해 각 시나리오가 동일한 초기 상태에서 독립적으로 실행됩니다.

---

## 5. 표준 샘플 코드 (Golden Sample)

### 5.1 Feature 파일

**`src/test/resources/features/reservation.feature`** (복합 기능 예시)

```gherkin
Feature: 예약 관리
  관리자가 예약을 생성하고 상태를 변경하여 예약을 관리한다

  Background: 관리자가 로그인되어 있다

  # ============================================================
  # 예약 생성
  # ============================================================

  # ==================== 정상 케이스 ====================

  Scenario: 유효한 정보로 예약을 생성한다
    Given 사이트번호가 "B-001"인 캠프사이트가 존재한다
    When 관리자가 고객명 "홍길동"으로 해당 캠프사이트에 "2025-06-01"부터 "2025-06-03"까지 예약을 생성한다
    Then 예약이 생성된다
    And 예약을 조회하면 고객명이 "홍길동"이다

  # ==================== 예외 케이스 ====================

  Scenario: 존재하지 않는 캠프사이트에 예약할 수 없다
    When 관리자가 존재하지 않는 캠프사이트에 예약을 생성한다
    Then 응답 상태 코드는 404이다

  Scenario: 같은 날짜에 이미 예약이 있는 캠프사이트에 예약할 수 없다
    Given 사이트번호가 "B-002"인 캠프사이트가 존재한다
    And 해당 캠프사이트에 "2025-07-01"부터 "2025-07-05"까지 예약이 존재한다
    When 관리자가 해당 캠프사이트에 "2025-07-03"부터 "2025-07-07"까지 예약을 생성한다
    Then 응답 상태 코드는 409이다

  # ==================== 엣지 케이스 ====================

  Scenario: 종료일이 시작일보다 빠르면 예약할 수 없다
    Given 사이트번호가 "B-003"인 캠프사이트가 존재한다
    When 관리자가 해당 캠프사이트에 "2025-08-05"부터 "2025-08-01"까지 예약을 생성한다
    Then 응답 상태 코드는 400이다

  Scenario: 고객명 없이 예약할 수 없다
    Given 사이트번호가 "B-004"인 캠프사이트가 존재한다
    When 관리자가 고객명 없이 해당 캠프사이트에 예약을 생성한다
    Then 응답 상태 코드는 400이다

  # ============================================================
  # 예약 상태 변경
  # ============================================================

  # ==================== 정상 케이스 ====================

  Scenario: 예약을 취소하면 해당 사이트는 다시 예약 가능하다
    When 관리자가 확정된 예약을 취소한다
    Then 예약을 조회하면 취소 상태이다
    And 해당 캠프사이트는 같은 날짜에 다시 예약이 가능하다

  # ==================== 예외 케이스 ====================

  Scenario: 유효하지 않은 상태로 변경할 수 없다
    When 관리자가 확정된 예약을 유효하지 않음 상태로 변경한다
    Then 응답 상태 코드는 400이다
    And 예약은 확정 상태로 유지된다

  Scenario: 존재하지 않는 예약은 상태를 변경할 수 없다
    When 관리자가 존재하지 않는 예약을 취소 상태로 변경한다
    Then 응답 상태 코드는 404이다

  # ==================== 엣지 케이스 ====================

  Scenario: 빈 상태값으로 예약 상태를 변경할 수 없다
    When 관리자가 확정된 예약의 상태값을 빈 값으로 예약 상태 변경을 요청한다
    Then 응답 상태 코드는 400이다
    And 예약은 확정 상태로 유지된다
```

### 5.2 Cucumber 실행 설정

**`src/test/java/com/camping/admin/CucumberTestRunner.java`**

```java
package com.camping.admin;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.camping.admin")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary")
public class CucumberTestRunner {
}
```

### 5.3 Spring Context 설정

**`src/test/java/com/camping/admin/CucumberSpringConfiguration.java`**

```java
package com.camping.admin;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
}
```

### 5.4 공통 Hooks

**`src/test/java/com/camping/admin/common/CommonHooks.java`**

```java
package com.camping.admin.common;

import com.camping.admin.CucumberSpringConfiguration;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.security.JwtService;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 모든 시나리오에서 공통으로 사용되는 Cucumber Hooks
 */
public class CommonHooks extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Value("${admin.username}")
    private String adminUsername;

    // 테스트 컨텍스트 공유용 (다른 Step/Helper 클래스에서 접근 가능)
    public static String adminToken;
    public static Response lastResponse;

    @Before(order = 0)
    public void cleanupDatabase() {
        // 1. 모든 테이블 초기화
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'",
                String.class
        );

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY");
        }

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        // 2. data.sql 시드 데이터 재삽입
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("data.sql"));
        } catch (SQLException e) {
            throw new RuntimeException("data.sql 실행 실패", e);
        }
    }

    @Before(order = 1)
    public void setupRestAssured() {
        RestAssured.port = port;
    }

    @Before(order = 2)
    public void setupAdminToken() {
        adminToken = jwtService.generateToken(adminUsername);
    }

    @Given("관리자가 로그인되어 있다")
    public void 관리자가_로그인되어_있다() {
        // @Before 훅에서 이미 처리됨 - Background 문서화 목적
    }

    // ==================== 공통 Then ====================

    @Then("응답 상태 코드는 {int}이다")
    public void 응답_상태_코드는_N이다(int expectedStatusCode) {
        assertThat(lastResponse.statusCode()).isEqualTo(expectedStatusCode);
    }

    // ==================== Parameter Types ====================

    @ParameterType("취소|확정|대기|유효하지 않음")
    public String 예약상태(String status) {
        ReservationStatus found = ReservationStatus.fromDisplayName(status);
        return found != null ? found.name() : status;
    }
}
```

### 5.5 Step 정의 클래스

**`src/test/java/com/camping/admin/steps/ReservationSteps.java`**

```java
package com.camping.admin.steps;

import com.camping.admin.CucumberSpringConfiguration;
import com.camping.admin.helper.ReservationTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 예약 관리 기능의 인수 테스트 Step 정의
 */
public class ReservationSteps extends CucumberSpringConfiguration {

    @Autowired
    private ReservationTestHelper helper;

    private static final Long 존재하지_않는_예약_ID = 999999L;
    private static final String EMPTY = "";

    // ==================== Given ====================

    @Given("사이트번호가 {string}인 캠프사이트가 존재한다")
    public void 사이트번호가_인_캠프사이트가_존재한다(String siteNumber) {
        helper.사이트번호로_캠프사이트를_준비한다(siteNumber);
    }

    @Given("해당 캠프사이트에 {string}부터 {string}까지 예약이 존재한다")
    public void 해당_캠프사이트에_부터_까지_예약이_존재한다(String startDate, String endDate) {
        helper.해당_캠프사이트에_예약을_생성한다(startDate, endDate);
    }

    // ==================== When - 예약 생성 ====================

    @When("관리자가 고객명 {string}으로 해당 캠프사이트에 {string}부터 {string}까지 예약을 생성한다")
    public void 관리자가_고객명으로_해당_캠프사이트에_예약을_생성한다(String customerName, String startDate, String endDate) {
        helper.고객명으로_예약을_요청한다(customerName, startDate, endDate);
    }

    @When("관리자가 존재하지 않는 캠프사이트에 예약을 생성한다")
    public void 관리자가_존재하지_않는_캠프사이트에_예약을_생성한다() {
        helper.존재하지_않는_캠프사이트에_예약을_요청한다();
    }

    @When("관리자가 해당 캠프사이트에 {string}부터 {string}까지 예약을 생성한다")
    public void 관리자가_해당_캠프사이트에_예약을_생성한다(String startDate, String endDate) {
        helper.해당_캠프사이트에_날짜로_예약을_요청한다(startDate, endDate);
    }

    @When("관리자가 고객명 없이 해당 캠프사이트에 예약을 생성한다")
    public void 관리자가_고객명_없이_해당_캠프사이트에_예약을_생성한다() {
        helper.고객명_없이_예약을_요청한다();
    }

    // ==================== When - 예약 상태 변경 ====================

    @When("관리자가 확정된 예약을 취소한다")
    public void 관리자가_확정된_예약을_취소한다() {
        helper.확정된_예약을_찾는다();
        helper.현재_예약의_상태를_변경한다("CANCELLED");
    }

    @When("관리자가 확정된 예약을 {예약상태} 상태로 변경한다")
    public void 관리자가_확정된_예약을_상태로_변경한다(String status) {
        helper.확정된_예약을_찾는다();
        helper.현재_예약의_상태를_변경한다(status);
    }

    @When("관리자가 존재하지 않는 예약을 {예약상태} 상태로 변경한다")
    public void 관리자가_존재하지_않는_예약을_상태로_변경한다(String status) {
        helper.예약_상태를_변경한다(존재하지_않는_예약_ID, status);
    }

    @When("관리자가 확정된 예약의 상태값을 빈 값으로 예약 상태 변경을 요청한다")
    public void 관리자가_확정된_예약의_상태값을_빈_값으로_예약_상태_변경을_요청한다() {
        helper.확정된_예약을_찾는다();
        helper.현재_예약의_상태를_변경한다(EMPTY);
    }

    // ==================== Then - 예약 생성 ====================

    @Then("예약이 생성된다")
    public void 예약이_생성된다() {
        helper.예약이_생성되었는지_검증한다();
    }

    @Then("예약을 조회하면 고객명이 {string}이다")
    public void 예약을_조회하면_고객명이_이다(String expectedCustomerName) {
        helper.예약을_조회하여_고객명을_검증한다(expectedCustomerName);
    }

    // ==================== Then - 예약 상태 변경 ====================

    @Then("예약을 조회하면 취소 상태이다")
    public void 예약을_조회하면_취소_상태이다() {
        helper.예약을_조회하여_상태를_검증한다("CANCELLED");
    }

    @Then("예약이 {예약상태}된다")
    public void 예약이_된다(String status) {
        helper.예약_상태를_검증한다(status);
    }

    @Then("해당 캠프사이트는 같은 날짜에 다시 예약이 가능하다")
    public void 해당_캠프사이트는_같은_날짜에_다시_예약이_가능하다() {
        helper.겹치는_예약이_없는지_검증한다();
    }

    @Then("예약은 {예약상태} 상태로 유지된다")
    public void 예약은_상태로_유지된다(String status) {
        helper.예약_상태를_검증한다(status);
    }
}
```

### 5.6 헬퍼 클래스

**`src/test/java/com/camping/admin/helper/ReservationTestHelper.java`**

```java
package com.camping.admin.helper;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.common.CommonHooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.camping.admin.apiExtractableresponse.ReservationApiExtractableResponse.예약을_생성한다;
import static com.camping.admin.apiExtractableresponse.ReservationApiExtractableResponse.예약의_상태를_변경한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 예약 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class ReservationTestHelper {

    private static final Long 존재하지_않는_캠프사이트_ID = 999999L;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private CampsiteTestHelper campsiteTestHelper;

    private Reservation currentReservation;
    private Campsite currentCampsite;

    // ==================== 캠프사이트 설정 (Given) ====================

    public void 사이트번호로_캠프사이트를_준비한다(String siteNumber) {
        campsiteTestHelper.사이트번호로_캠프사이트를_생성한다(siteNumber);
        this.currentCampsite = campsiteRepository.findBySiteNumber(siteNumber)
                .orElseThrow(() -> new IllegalStateException("캠프사이트를 찾을 수 없습니다: " + siteNumber));
    }

    // ==================== 예약 설정 (Given) ====================

    public void 해당_캠프사이트에_예약을_생성한다(String startDate, String endDate) {
        Reservation reservation = new Reservation("사전예약고객", LocalDate.parse(startDate), LocalDate.parse(endDate), currentCampsite);
        reservationRepository.save(reservation);
    }

    // ==================== 예약 조회 (Given/When) ====================

    public Reservation 첫번째_예약을_조회한다() {
        return reservationRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("예약이 존재하지 않습니다."));
    }

    public void 확정된_예약을_찾는다() {
        this.currentReservation = reservationRepository.findAll().stream()
                .filter(r -> ReservationStatus.CONFIRMED.name().equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("확정 상태인 예약이 존재하지 않습니다."));
    }

    // ==================== API 호출 - 예약 생성 (When) ====================

    public void 고객명으로_예약을_요청한다(String customerName, String startDate, String endDate) {
        Map<String, Object> body = new HashMap<>();
        body.put("campsiteId", currentCampsite.getId());
        body.put("customerName", customerName);
        body.put("startDate", startDate);
        body.put("endDate", endDate);
        CommonHooks.lastResponse = 예약을_생성한다(body);
    }

    public void 존재하지_않는_캠프사이트에_예약을_요청한다() {
        Map<String, Object> body = new HashMap<>();
        body.put("campsiteId", 존재하지_않는_캠프사이트_ID);
        body.put("customerName", "테스트고객");
        body.put("startDate", "2025-06-01");
        body.put("endDate", "2025-06-03");
        CommonHooks.lastResponse = 예약을_생성한다(body);
    }

    // ==================== API 호출 - 예약 상태 변경 (When) ====================

    public void 예약_상태를_변경한다(Long reservationId, String status) {
        CommonHooks.lastResponse = 예약의_상태를_변경한다(reservationId, status);
    }

    public void 현재_예약의_상태를_변경한다(String status) {
        예약_상태를_변경한다(currentReservation.getId(), status);
    }

    // ==================== 검증 - 예약 생성 (Then) ====================

    public void 예약이_생성되었는지_검증한다() {
        assertThat(CommonHooks.lastResponse.statusCode()).isEqualTo(201);
    }

    public void 예약을_조회하여_고객명을_검증한다(String expectedCustomerName) {
        Long createdId = CommonHooks.lastResponse.jsonPath().getLong("id");
        Reservation reservation = reservationRepository.findById(createdId)
                .orElseThrow(() -> new AssertionError("생성된 예약을 찾을 수 없습니다. ID: " + createdId));
        assertThat(reservation.getCustomerName()).isEqualTo(expectedCustomerName);
    }

    // ==================== 검증 - 예약 상태 변경 (Then) ====================

    public void 예약_상태를_검증한다(String status) {
        assertThat(현재_예약을_다시_조회한다().getStatus())
                .isEqualTo(status);
    }

    public void 겹치는_예약이_없는지_검증한다() {
        assertThat(reservationRepository.findOverlappingReservations(
                currentReservation.getCampsite().getId(),
                currentReservation.getStartDate(),
                currentReservation.getEndDate()
        )).isEmpty();
    }

    // ==================== Private ====================

    private Reservation 현재_예약을_다시_조회한다() {
        return reservationRepository.findById(currentReservation.getId()).orElseThrow();
    }
}
```

### 5.7 API 호출 클래스

**`src/test/java/com/camping/admin/apiExtractableresponse/ReservationApiExtractableResponse.java`**

```java
package com.camping.admin.apiExtractableresponse;

import com.camping.admin.common.CommonHooks;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationApiExtractableResponse {

    public static Response 예약을_생성한다(Map<String, Object> requestBody) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/admin/reservations");
    }

    public static Response 예약의_상태를_변경한다(Long id, String status) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"" + status + "\"}")
                .when()
                .patch("/admin/reservations/" + id + "/status");
    }
}
```

---

## 6. 테스트 실행

```bash
# 전체 인수 테스트 실행
./gradlew test --tests "com.camping.admin.CucumberTestRunner"

# 특정 Feature 파일만 실행 (Cucumber 옵션 활용)
./gradlew test -Dcucumber.filter.tags="@reservation"
```

---

## 7. 체크리스트

새로운 도메인의 인수 테스트를 추가할 때 다음을 확인하세요:

- [ ] Feature 파일 생성 (`src/test/resources/features/{domain}.feature`)
- [ ] Steps 클래스 생성 (`src/test/java/.../steps/{Domain}Steps.java`)
- [ ] Helper 클래스 생성 (`src/test/java/.../helper/{Domain}TestHelper.java`)
- [ ] API Response 클래스 생성 (`src/test/java/.../apiExtractableresponse/{Domain}ApiExtractableResponse.java`)
- [ ] Steps 클래스가 `CucumberSpringConfiguration`을 상속하는지 확인
- [ ] Helper 클래스에 `@Component` 어노테이션이 있는지 확인
- [ ] API 응답을 `CommonHooks.lastResponse`에 저장하는지 확인
- [ ] 필요시 CommonHooks에 ParameterType 추가
- [ ] 한글 메서드명 규칙 준수
- [ ] 섹션 주석 형식 준수
