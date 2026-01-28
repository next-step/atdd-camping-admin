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
}
```

---

## 2. 파일 위치 규칙

```
src/test/
├── java/com/camping/admin/
│   ├── CucumberTestRunner.java          # Cucumber 실행 진입점
│   ├── steps/                           # Step 정의 패키지
│   │   ├── CucumberSpringConfiguration.java  # Spring Context 설정
│   │   ├── CommonHooks.java             # 공통 Hooks & ParameterType
│   │   ├── {Domain}Steps.java           # 도메인별 Step 정의
│   │   └── {Domain}TestHelper.java      # 도메인별 헬퍼 클래스
│   └── apiExtractableresponse/          # API 호출 유틸리티
│       └── {Domain}ApiExtractableResponse.java
└── resources/
    └── features/                        # Gherkin Feature 파일
        └── {domain}.feature
```

### 파일별 역할

| 파일 | 역할 |
|------|------|
| `CucumberTestRunner.java` | Cucumber 테스트 Suite 실행 진입점 |
| `CucumberSpringConfiguration.java` | `@CucumberContextConfiguration` + `@SpringBootTest` 설정 |
| `CommonHooks.java` | `@Before` 훅, `@ParameterType` 정의, 공통 `@Given` 스텝 |
| `{Domain}Steps.java` | Gherkin과 Helper를 연결하는 Step 정의 |
| `{Domain}TestHelper.java` | 테스트 상태 관리, Repository 접근, 검증 로직 |
| `{Domain}ApiExtractableResponse.java` | RestAssured 기반 API 호출 메서드 |
| `{domain}.feature` | Gherkin 시나리오 정의 |

---

## 3. 코딩 컨벤션

### 3.1 클래스 네이밍

| 유형 | 네이밍 규칙 | 예시 |
|------|-------------|------|
| Step 정의 클래스 | `{Domain}Steps` | `ReservationSteps` |
| 헬퍼 클래스 | `{Domain}TestHelper` | `ReservationTestHelper` |
| API 호출 클래스 | `{Domain}ApiExtractableResponse` | `ReservationApiExtractableResponse` |
| Feature 파일 | `{domain}.feature` (소문자) | `reservation.feature` |

### 3.2 메서드 네이밍

**한글 메서드명을 사용하여 가독성을 극대화합니다.**

| 유형 | 네이밍 패턴 | 예시 |
|------|-------------|------|
| Step 정의 | Gherkin 문장을 snake_case로 변환 | `관리자가_확정된_예약을_상태로_변경한다()` |
| Helper - 조회 | `{대상}을_찾는다` | `확정된_예약을_찾는다()` |
| Helper - API 호출 | `{행위}한다` | `현재_예약의_상태를_변경한다()` |
| Helper - 검증 | `{조건}를_검증한다` | `응답_상태_코드를_검증한다()` |
| API Response | `{행위}한다` | `예약의_상태를_변경한다()` |

### 3.3 내부 주석 스타일

섹션 구분을 위해 다음 형식의 주석을 사용합니다:

```java
// ==================== 섹션명 ====================
```

**표준 섹션 구분:**

```java
// Steps 클래스
// ==================== When ====================
// ==================== Then ====================

// Helper 클래스
// ==================== 예약 조회 (Given/When) ====================
// ==================== API 호출 (When) ====================
// ==================== 검증 (Then) ====================
// ==================== Private ====================
```

### 3.4 Feature 파일 작성 규칙

```gherkin
Feature: {기능명}
  {기능 설명}

  Background: {공통 전제 조건}

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
│     (테스트 상태 관리, Repository 접근, 검증 로직 수행)          │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│           API Response 클래스 (*ApiExtractableResponse.java) │
│              (RestAssured 기반 HTTP 요청 수행)                │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 핵심 원칙

1. **Steps는 얇게 유지**: Step 정의 클래스는 Gherkin과 Helper를 연결하는 역할만 수행
2. **Helper에 로직 집중**: 테스트 상태 관리, Repository 접근, 검증 로직은 Helper에서 수행
3. **API 호출 분리**: HTTP 요청은 ApiExtractableResponse 클래스에서 static 메서드로 제공
4. **공통 설정은 Hooks에서**: RestAssured 설정, 인증 토큰 등은 CommonHooks에서 관리

---

## 5. 표준 샘플 코드 (Golden Sample)

### 5.1 Feature 파일

**`src/test/resources/features/reservation.feature`**

```gherkin
Feature: 예약 상태 변경
  관리자가 예약의 상태를 변경하여 예약을 관리한다

  Background: 관리자가 로그인을 했다

  # ==================== 정상 케이스 ====================

  Scenario: 예약을 취소하면 해당 사이트는 다시 예약 가능하다
    When 관리자가 확정된 예약을 취소 상태로 변경한다
    Then 응답 상태 코드는 200이다
    And 예약이 취소된다
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
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.camping.admin.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary")
public class CucumberTestRunner {
}
```

### 5.3 Spring Context 설정

**`src/test/java/com/camping/admin/steps/CucumberSpringConfiguration.java`**

```java
package com.camping.admin.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
}
```

### 5.4 공통 Hooks

**`src/test/java/com/camping/admin/steps/CommonHooks.java`**

```java
package com.camping.admin.steps;

import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.security.JwtService;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * 모든 시나리오에서 공통으로 사용되는 Cucumber Hooks
 */
public class CommonHooks extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Value("${admin.username}")
    private String adminUsername;

    // 테스트 컨텍스트 공유용 (다른 Step 클래스에서 접근 가능)
    public static String adminToken;

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.port = port;
    }

    @Before(order = 1)
    public void setupAdminToken() {
        adminToken = jwtService.generateToken(adminUsername);
    }

    @Given("관리자가 로그인을 했다")
    public void 관리자가_로그인을_했다() {
        // @Before 훅에서 이미 처리됨 - Background 문서화 목적
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

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 예약 상태 변경 기능의 인수 테스트 Step 정의
 */
public class ReservationSteps extends CucumberSpringConfiguration {

    @Autowired
    private ReservationTestHelper helper;

    private static final Long 존재하지_않는_예약_ID = 999999L;
    private static final String EMPTY = "";

    // ==================== When ====================

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

    // ==================== Then ====================

    @Then("응답 상태 코드는 {int}이다")
    public void 응답_상태_코드는_N이다(int expectedStatusCode) {
        helper.응답_상태_코드를_검증한다(expectedStatusCode);
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

**`src/test/java/com/camping/admin/steps/ReservationTestHelper.java`**

```java
package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.camping.admin.apiExtractableresponse.ReservationApiExtractableResponse.예약의_상태를_변경한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 예약 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class ReservationTestHelper {

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation currentReservation;
    private Response lastResponse;

    // ==================== 예약 조회 (Given/When) ====================

    public void 확정된_예약을_찾는다() {
        this.currentReservation = reservationRepository.findAll().stream()
                .filter(r -> ReservationStatus.CONFIRMED.name().equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("확정 상태인 예약이 존재하지 않습니다."));
    }

    // ==================== API 호출 (When) ====================

    public void 예약_상태를_변경한다(Long reservationId, String status) {
        this.lastResponse = 예약의_상태를_변경한다(reservationId, status);
    }

    public void 현재_예약의_상태를_변경한다(String status) {
        예약_상태를_변경한다(currentReservation.getId(), status);
    }

    // ==================== 검증 (Then) ====================

    public void 응답_상태_코드를_검증한다(int expectedStatusCode) {
        assertThat(lastResponse.statusCode()).isEqualTo(expectedStatusCode);
    }

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

import com.camping.admin.steps.CommonHooks;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ReservationApiExtractableResponse {

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
- [ ] Steps 클래스 생성 (`{Domain}Steps.java`)
- [ ] Helper 클래스 생성 (`{Domain}TestHelper.java`)
- [ ] API Response 클래스 생성 (`{Domain}ApiExtractableResponse.java`)
- [ ] 필요시 CommonHooks에 ParameterType 추가
- [ ] 한글 메서드명 규칙 준수
- [ ] 섹션 주석 형식 준수