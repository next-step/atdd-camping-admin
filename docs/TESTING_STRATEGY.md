# 인수 테스트 전략 (Acceptance Testing Strategy)

## 목차
1. [핵심 기술 스택](#1-핵심-기술-스택)
2. [코딩 컨벤션](#2-코딩-컨벤션)
3. [파일 위치 규칙](#3-파일-위치-규칙)
4. [골든 샘플 코드](#4-골든-샘플-코드)

---

## 1. 핵심 기술 스택

| 기술 | 버전 | 용도 |
|------|------|------|
| **JUnit Platform Suite** | 1.10.0 | Cucumber 테스트 실행기 |
| **Cucumber** | 7.14.0 | BDD 기반 인수 테스트 |
| **RestAssured** | 5.3.2 | REST API 테스트 |
| **AssertJ** | (Spring Boot 내장) | 가독성 높은 단언문 |
| **Spring Boot Test** | 3.2.0 | 통합 테스트 환경 |
| **H2 Database** | (Spring Boot 내장) | 인메모리 테스트 DB |

### 1.1 의존성 (build.gradle)

```groovy
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

    // Spring Boot Test (AssertJ 포함)
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

---

## 2. 코딩 컨벤션

### 2.1 네이밍 규칙

| 대상 | 규칙 | 예시 |
|------|------|------|
| Feature 파일 | 기능명(영문, snake_case) | `reservation.feature`, `rental_create.feature` |
| Steps 클래스 | `[기능명]Steps` | `ReservationSteps`, `RentalSteps` |
| 상수 클래스 | `TestConstants` | `TestConstants.java` |
| 상수명 | 한글 + snake_case | `예약상태_취소`, `상품유형_대여` |
| Step 메서드 | Gherkin 문장과 동일 (한글) | `사용자가_사이트를_예약했다()` |

### 2.2 Step 메서드 작성 규칙

```java
// ✅ 올바른 예시: 한글 메서드명, Gherkin 문장과 동일
@Given("사용자가 {long}번 사이트가 예약 되어있다")
public void 사용자가_사이트를_예약했다(Long siteNo) {
    // ...
}

// ❌ 잘못된 예시: 영문 메서드명
@Given("사용자가 {long}번 사이트가 예약 되어있다")
public void userHasReservedSite(Long siteNo) {
    // ...
}
```

### 2.3 Import 규칙

```java
// Static Import 사용
import static com.camping.admin.TestConstants.예약상태_취소;
import static org.assertj.core.api.Assertions.assertThat;

// RestAssured는 클래스명.메서드() 형식
import io.restassured.RestAssured;
RestAssured.given()...
```

### 2.4 주석 스타일

```java
// 클래스 레벨: 기능 설명 (선택)
/**
 * 예약 관리 기능의 인수 테스트 Step 정의
 */
public class ReservationSteps extends CucumberSpringConfiguration {

    // 필드 레벨: 주석 없음 (자명한 경우)
    @LocalServerPort
    private int port;

    // @Before 레벨: order 설명 (필요시)
    @Before(order = 0)  // RestAssured 설정은 가장 먼저 실행
    public void setupRestAssured() {
        RestAssured.port = port;
    }
}
```

### 2.5 RestAssured 작성 스타일

```java
// Given-When-Then 체이닝으로 작성
RestAssured.given()
        .header("Authorization", "Bearer " + adminToken)  // 인증 헤더
        .contentType(ContentType.JSON)                     // Content-Type
        .body(requestBody)                                 // 요청 본문
        .when()
        .patch("/admin/reservations/" + id + "/status")    // HTTP 메서드 + URI
        .then()
        .statusCode(200)                                   // 상태 코드 검증
        .extract()
        .jsonPath()
        .getString("status");                              // 응답 값 추출
```

### 2.6 단언문 (Assertion) 스타일

```java
// AssertJ 사용 (JUnit Assertions 사용 금지)
// ✅ 올바른 예시
assertThat(status).isEqualTo(예약상태_취소);
assertThat(reservations).isEmpty();
assertThat(result).isNotNull();

// ❌ 잘못된 예시 (JUnit)
assertEquals(예약상태_취소, status);
assertTrue(reservations.isEmpty());
```

---

## 3. 파일 위치 규칙

### 3.1 디렉토리 구조

```
src/test/
├── java/com/camping/admin/
│   ├── CucumberTestRunner.java          # Cucumber 실행 진입점
│   ├── TestConstants.java               # 테스트 상수 정의
│   └── steps/
│       ├── CucumberSpringConfiguration.java  # Spring 연동 설정
│       ├── ReservationSteps.java        # 예약 기능 Steps
│       ├── RentalSteps.java             # 대여 기능 Steps
│       ├── ProductSteps.java            # 상품 기능 Steps
│       └── CampsiteSteps.java           # 캠프사이트 기능 Steps
│
└── resources/
    └── features/
        ├── reservation.feature          # 예약 시나리오
        ├── rental.feature               # 대여 시나리오
        ├── product.feature              # 상품 시나리오
        └── campsite.feature             # 캠프사이트 시나리오
```

### 3.2 파일별 역할

| 파일 | 역할 | 위치 |
|------|------|------|
| `CucumberTestRunner.java` | Cucumber 테스트 실행 진입점 | `src/test/java/.../` |
| `CucumberSpringConfiguration.java` | Spring Context 연동 설정 | `src/test/java/.../steps/` |
| `TestConstants.java` | 테스트 상수 (상태값, 메시지 등) | `src/test/java/.../` |
| `[기능]Steps.java` | Gherkin Step 구현체 | `src/test/java/.../steps/` |
| `[기능].feature` | Gherkin 시나리오 정의 | `src/test/resources/features/` |

### 3.3 Feature 파일 ↔ Steps 클래스 매핑

| Feature 파일 | Steps 클래스 |
|--------------|-------------|
| `reservation.feature` | `ReservationSteps.java` |
| `rental.feature` | `RentalSteps.java` |
| `product.feature` | `ProductSteps.java` |
| `campsite.feature` | `CampsiteSteps.java` |

---

## 4. 골든 샘플 코드

### 4.1 CucumberTestRunner.java

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

### 4.2 CucumberSpringConfiguration.java

```java
package com.camping.admin.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
}
```

### 4.3 TestConstants.java

```java
package com.camping.admin;

/**
 * 테스트에서 사용하는 상수 정의
 * - 상태값, 에러 메시지 등을 한글 상수로 관리
 * - 실제 API 값과 매핑하여 가독성 향상
 */
public class TestConstants {

    // ==================== 예약 상태 ====================
    public static final String 예약상태_대기 = "WAITING";
    public static final String 예약상태_확정 = "CONFIRMED";
    public static final String 예약상태_취소 = "CANCELLED";
    public static final String 예약상태_체크인 = "CHECKED_IN";
    public static final String 예약상태_체크아웃 = "CHECKED_OUT";

    // ==================== 상품 유형 ====================
    public static final String 상품유형_판매 = "SALE";
    public static final String 상품유형_대여 = "RENTAL";

    // ==================== API 경로 ====================
    public static final String API_예약 = "/admin/reservations";
    public static final String API_상품 = "/admin/products";
    public static final String API_캠프사이트 = "/admin/campsites";
    public static final String API_대여 = "/admin/rentals";
}
```

### 4.4 Feature 파일 (rental.feature)

```gherkin
# language: ko

Feature: 대여 생성
  관리자가 대여용 상품을 고객에게 대여하고 재고를 관리한다

  Scenario: 대여용 상품을 예약 고객에게 정상 대여한다
    Given 대여용 상품이 재고 10개로 등록되어 있다
    And 예약이 1건 존재한다
    When 관리자가 해당 상품 3개를 예약에 연결하여 대여한다
    Then 대여가 성공한다
    And 대여 기록이 생성된다
    And 상품 재고가 7개로 감소한다

  Scenario: 재고보다 많은 수량을 대여하면 실패한다
    Given 대여용 상품이 재고 5개로 등록되어 있다
    When 관리자가 해당 상품 10개를 대여한다
    Then 재고 부족으로 대여되지 않는다
    And 상품 재고는 5개로 유지된다
```

### 4.5 Steps 클래스 (RentalSteps.java)

```java
package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.security.JwtService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static com.camping.admin.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대여 기능 인수 테스트 Step 정의
 */
public class RentalSteps extends CucumberSpringConfiguration {

    // ==================== 인프라 설정 ====================

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RentalRecordRepository rentalRecordRepository;

    @Autowired
    private JwtService jwtService;

    @Value("${admin.username}")
    private String adminUsername;

    // ==================== 테스트 상태 ====================

    private String adminToken;
    private Product savedProduct;
    private Reservation savedReservation;
    private Response response;
    private int initialStock;

    // ==================== 설정 (@Before) ====================

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.port = port;
    }

    @Before(order = 1)
    public void setupAdminToken() {
        this.adminToken = jwtService.generateToken(adminUsername);
    }

    @Before(order = 2)
    public void cleanupDatabase() {
        rentalRecordRepository.deleteAll();
    }

    // ==================== Given ====================

    @Given("대여용 상품이 재고 {int}개로 등록되어 있다")
    public void 대여용_상품이_재고_N개로_등록되어_있다(int stockQuantity) {
        Product product = new Product(
                "테스트 대여 상품",
                stockQuantity,
                BigDecimal.valueOf(10000),
                ProductType.RENTAL
        );
        this.savedProduct = productRepository.save(product);
        this.initialStock = stockQuantity;
    }

    @Given("예약이 {int}건 존재한다")
    public void 예약이_N건_존재한다(int count) {
        this.savedReservation = reservationRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("예약 데이터가 필요합니다."));
    }

    // ==================== When ====================

    @When("관리자가 해당 상품 {int}개를 예약에 연결하여 대여한다")
    public void 관리자가_해당_상품_N개를_예약에_연결하여_대여한다(int quantity) {
        String requestBody = String.format("""
                {
                    "productId": %d,
                    "quantity": %d,
                    "reservationId": %d
                }
                """, savedProduct.getId(), quantity, savedReservation.getId());

        this.response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(API_대여);
    }

    @When("관리자가 해당 상품 {int}개를 대여한다")
    public void 관리자가_해당_상품_N개를_대여한다(int quantity) {
        String requestBody = String.format("""
                {
                    "productId": %d,
                    "quantity": %d
                }
                """, savedProduct.getId(), quantity);

        this.response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(API_대여);
    }

    // ==================== Then ====================

    @Then("대여가 성공한다")
    public void 대여가_성공한다() {
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Then("대여 기록이 생성된다")
    public void 대여_기록이_생성된다() {
        Long rentalId = response.jsonPath().getLong("id");
        assertThat(rentalId).isNotNull();
        assertThat(rentalRecordRepository.findById(rentalId)).isPresent();
    }

    @Then("상품 재고가 {int}개로 감소한다")
    public void 상품_재고가_N개로_감소한다(int expectedStock) {
        Product product = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(product.getStockQuantity()).isEqualTo(expectedStock);
    }

    @Then("재고 부족으로 대여되지 않는다")
    public void 재고_부족으로_대여되지_않는다() {
        assertThat(response.statusCode()).isIn(400, 500);
    }

    @Then("상품 재고는 {int}개로 유지된다")
    public void 상품_재고는_N개로_유지된다(int expectedStock) {
        Product product = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(product.getStockQuantity()).isEqualTo(expectedStock);
    }
}
```

---

## 부록: 테스트 실행 방법

### Gradle 명령어
```bash
# 전체 테스트 실행
./gradlew test

# 특정 Feature 실행
./gradlew test --tests "CucumberTestRunner"
```

### IDE에서 실행
1. `CucumberTestRunner.java` 파일 열기
2. 클래스명 옆 실행 버튼 클릭
3. 또는 `*.feature` 파일에서 Scenario 단위 실행

---

*작성일: 2026-01-26*
*기준 프로젝트: ATDD Camping Admin System*