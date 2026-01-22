# Cucumber 인수테스트 작성 가이드

## 목차
1. [프로젝트 구조](#1-프로젝트-구조)
2. [핵심 원칙](#2-핵심-원칙)
3. [Feature 파일 작성법](#3-feature-파일-작성법)
4. [Step 클래스 작성법](#4-step-클래스-작성법)
5. [완성된 예시 코드](#5-완성된-예시-코드)
6. [공통 유틸리티](#6-공통-유틸리티)
7. [체크리스트](#7-체크리스트)

---

## 1. 프로젝트 구조

```
src/test/
├── java/com/camping/admin/
│   ├── CucumberTestRunner.java          # 테스트 실행 진입점
│   └── steps/
│       ├── CucumberSpringConfiguration.java  # Spring 설정
│       ├── CommonSteps.java             # 공통 Step (로그인 등)
│       ├── ReservationSteps.java        # 예약 관련 Step
│       ├── RentalSteps.java             # 대여 관련 Step
│       └── SalesSteps.java              # 판매 관련 Step
└── resources/
    └── features/
        ├── reservation_cancel.feature
        ├── rental_create.feature
        └── sales_process.feature
```

---

## 2. 핵심 원칙

### 2.1 Feature 파일 작성 원칙

| 원칙 | 설명 |
|------|------|
| **비즈니스 언어 사용** | 기술 용어 대신 도메인 용어 사용 |
| **독립적인 시나리오** | 각 시나리오는 다른 시나리오에 의존하지 않음 |
| **명확한 Given-When-Then** | Given(전제조건), When(행동), Then(결과) 구분 |

### 2.2 Step 클래스 작성 원칙

| 원칙 | 설명 |
|------|------|
| **상속 금지** | `CucumberSpringConfiguration`을 상속하지 않음 |
| **단일 책임** | 하나의 Step 클래스는 하나의 도메인만 담당 |
| **상태 공유** | 시나리오 간 상태는 인스턴스 변수로 관리 |
| **API 호출** | RestAssured를 통한 실제 HTTP 요청 |

---

## 3. Feature 파일 작성법

### 3.1 기본 구조

```gherkin
Feature: [기능명]
  [기능에 대한 간단한 설명 (선택)]

  Background:                    # 모든 시나리오 공통 전제조건 (선택)
    Given [공통 전제조건]

  Scenario: [시나리오명]
    Given [전제조건]
    When [행동]
    Then [기대결과]
```

### 3.2 좋은 예시 vs 나쁜 예시

**나쁜 예시 (기술적 표현)**
```gherkin
Scenario: POST /admin/reservations/1/status 호출 시 200 반환
  Given DB에 reservation 테이블에 id=1 데이터가 있다
  When PATCH 요청을 보낸다
  Then HTTP 200이 반환된다
```

**좋은 예시 (비즈니스 표현)**
```gherkin
Scenario: 관리자가 확정된 예약을 취소한다
  Given "김철수" 고객의 예약이 "확정" 상태로 존재한다
  When 관리자가 해당 예약을 "취소" 처리한다
  Then 예약 상태가 "취소"로 변경된다
```

### 3.3 파라미터 활용

```gherkin
Scenario Outline: 예약 상태 변경
  Given 예약이 "<현재상태>" 상태로 존재한다
  When 관리자가 해당 예약을 "<변경상태>" 처리한다
  Then 예약 상태가 "<변경상태>"로 변경된다

  Examples:
    | 현재상태 | 변경상태   |
    | 확정     | 취소      |
    | 대기     | 확정      |
    | 확정     | 체크인    |
```

---

## 4. Step 클래스 작성법

### 4.1 기본 구조

```java
package com.camping.admin.steps;

import io.cucumber.java.ko.*;  // 한글 어노테이션
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;

    // 시나리오 내 상태 공유용
    private ExtractableResponse<Response> response;
    private Long reservationId;

    // Step 구현...
}
```

### 4.2 한글 어노테이션 사용

```java
import io.cucumber.java.ko.*;  // 한글 전용

@주어진("예약이 존재한다")
public void 예약이_존재한다() { }

@만약("관리자가 예약을 취소하면")
public void 관리자가_예약을_취소하면() { }

@그러면("예약이 취소된다")
public void 예약이_취소된다() { }
```

또는 영어 어노테이션 사용:

```java
import io.cucumber.java.en.*;  // 영어

@Given("예약이 존재한다")
public void 예약이_존재한다() { }

@When("관리자가 예약을 취소하면")
public void 관리자가_예약을_취소하면() { }

@Then("예약이 취소된다")
public void 예약이_취소된다() { }
```

---

## 5. 완성된 예시 코드

### 5.1 Feature 파일

**`src/test/resources/features/reservation_cancel.feature`**

```gherkin
Feature: 관리자의 예약 취소 기능
  관리자는 고객의 예약을 취소할 수 있다.

  Scenario: 확정된 예약을 취소한다
    Given "김철수" 고객의 "A-001" 캠프사이트 예약이 "CONFIRMED" 상태로 존재한다
    When 관리자가 해당 예약을 "CANCELLED" 상태로 변경한다
    Then 예약 상태가 "CANCELLED"로 변경된다

  Scenario: 존재하지 않는 예약을 취소하면 실패한다
    Given 예약 ID "9999"는 존재하지 않는다
    When 관리자가 예약 ID "9999"를 취소하려고 한다
    Then 요청이 실패한다
```

### 5.2 Step 클래스

**`src/test/java/com/camping/admin/steps/ReservationSteps.java`**

```java
package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    // 시나리오 내 상태 공유
    private ExtractableResponse<Response> response;
    private Long reservationId;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    // ===== Given =====

    @Given("{string} 고객의 {string} 캠프사이트 예약이 {string} 상태로 존재한다")
    public void 예약이_존재한다(String customerName, String siteNumber, String status) {
        // 1. 캠프사이트 준비
        Campsite campsite = campsiteRepository.findBySiteNumber(siteNumber)
                .orElseGet(() -> campsiteRepository.save(
                        Campsite.builder()
                                .siteNumber(siteNumber)
                                .description("테스트 사이트")
                                .maxPeople(4)
                                .build()
                ));

        // 2. 예약 생성
        Reservation reservation = Reservation.builder()
                .customerName(customerName)
                .campsite(campsite)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .reservationDate(LocalDate.now())
                .status(status)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        this.reservationId = saved.getId();
    }

    @Given("예약 ID {string}는 존재하지 않는다")
    public void 예약이_존재하지_않는다(String id) {
        this.reservationId = Long.parseLong(id);
        reservationRepository.deleteById(this.reservationId);
    }

    // ===== When =====

    @When("관리자가 해당 예약을 {string} 상태로 변경한다")
    public void 예약_상태를_변경한다(String newStatus) {
        Map<String, String> body = new HashMap<>();
        body.put("status", newStatus);

        this.response = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                .when()
                    .patch("/admin/reservations/{id}/status", reservationId)
                .then()
                    .extract();
    }

    @When("관리자가 예약 ID {string}를 취소하려고 한다")
    public void 존재하지_않는_예약을_취소한다(String id) {
        Map<String, String> body = new HashMap<>();
        body.put("status", "CANCELLED");

        this.response = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                .when()
                    .patch("/admin/reservations/{id}/status", Long.parseLong(id))
                .then()
                    .extract();
    }

    // ===== Then =====

    @Then("예약 상태가 {string}로 변경된다")
    public void 예약_상태_확인(String expectedStatus) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("status")).isEqualTo(expectedStatus);

        // DB에서도 확인
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        assertThat(reservation.getStatus()).isEqualTo(expectedStatus);
    }

    @Then("요청이 실패한다")
    public void 요청이_실패한다() {
        assertThat(response.statusCode()).isGreaterThanOrEqualTo(400);
    }
}
```

### 5.3 대여 생성 예시

**`src/test/resources/features/rental_create.feature`**

```gherkin
Feature: 장비 대여 기능
  고객은 캠핑 장비를 대여할 수 있다.

  Background:
    Given "텐트" 상품이 재고 10개로 등록되어 있다

  Scenario: 예약 고객이 장비를 대여한다
    Given "김철수" 고객의 예약이 존재한다
    When 해당 예약에 "텐트" 2개를 대여한다
    Then 대여가 성공한다
    And "텐트" 재고가 8개로 감소한다

  Scenario: 재고가 부족하면 대여가 실패한다
    Given "김철수" 고객의 예약이 존재한다
    When 해당 예약에 "텐트" 15개를 대여한다
    Then 대여가 실패한다
    And 에러 메시지에 "재고 부족"이 포함된다
```

**`src/test/java/com/camping/admin/steps/RentalSteps.java`**

```java
package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.ReservationRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RentalSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private ExtractableResponse<Response> response;
    private Long reservationId;
    private Long productId;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    // ===== Given =====

    @Given("{string} 상품이 재고 {int}개로 등록되어 있다")
    public void 상품이_등록되어_있다(String productName, int stock) {
        Product product = productRepository.findByName(productName)
                .orElseGet(() -> productRepository.save(
                        Product.builder()
                                .name(productName)
                                .stockQuantity(stock)
                                .price(BigDecimal.valueOf(10000))
                                .productType(ProductType.RENTAL)
                                .build()
                ));

        // 재고 수량 업데이트
        product.setStockQuantity(stock);
        productRepository.save(product);
        this.productId = product.getId();
    }

    @Given("{string} 고객의 예약이 존재한다")
    public void 고객의_예약이_존재한다(String customerName) {
        Reservation reservation = reservationRepository.findAll().stream()
                .filter(r -> r.getCustomerName().equals(customerName))
                .findFirst()
                .orElseGet(() -> reservationRepository.save(
                        Reservation.builder()
                                .customerName(customerName)
                                .status("CONFIRMED")
                                .build()
                ));
        this.reservationId = reservation.getId();
    }

    // ===== When =====

    @When("해당 예약에 {string} {int}개를 대여한다")
    public void 장비를_대여한다(String productName, int quantity) {
        Product product = productRepository.findByName(productName).orElseThrow();

        Map<String, Object> body = new HashMap<>();
        body.put("reservationId", reservationId);
        body.put("productId", product.getId());
        body.put("quantity", quantity);

        this.response = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                .when()
                    .post("/admin/rentals")
                .then()
                    .extract();
    }

    // ===== Then =====

    @Then("대여가 성공한다")
    public void 대여가_성공한다() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Then("대여가 실패한다")
    public void 대여가_실패한다() {
        assertThat(response.statusCode()).isGreaterThanOrEqualTo(400);
    }

    @And("{string} 재고가 {int}개로 감소한다")
    public void 재고가_감소한다(String productName, int expectedStock) {
        Product product = productRepository.findByName(productName).orElseThrow();
        assertThat(product.getStockQuantity()).isEqualTo(expectedStock);
    }

    @And("에러 메시지에 {string}이 포함된다")
    public void 에러_메시지_확인(String expectedMessage) {
        String responseBody = response.body().asString();
        assertThat(responseBody).contains(expectedMessage);
    }
}
```

---

## 6. 공통 유틸리티

### 6.1 데이터 정리 (DatabaseCleanup)

**`src/test/java/com/camping/admin/steps/DatabaseCleanup.java`**

```java
package com.camping.admin.steps;

import io.cucumber.java.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseCleanup {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void cleanup() {
        // 외래키 제약 비활성화
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        // 테이블 데이터 삭제 (순서 중요하지 않음)
        jdbcTemplate.execute("TRUNCATE TABLE rental_record");
        jdbcTemplate.execute("TRUNCATE TABLE sales_record");
        jdbcTemplate.execute("TRUNCATE TABLE reservation");
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE campsite");

        // 외래키 제약 활성화
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
```

### 6.2 인증이 필요한 API 테스트

```java
public class AuthenticatedSteps {

    private String accessToken;

    @Given("관리자로 로그인한다")
    public void 관리자_로그인() {
        Map<String, String> loginRequest = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        this.accessToken = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(loginRequest)
                .when()
                    .post("/auth/login")
                .then()
                    .extract()
                    .jsonPath()
                    .getString("accessToken");
    }

    @When("인증된 요청으로 예약 목록을 조회한다")
    public void 인증된_요청() {
        response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + accessToken)
                .when()
                    .get("/admin/reservations")
                .then()
                    .extract();
    }
}
```

---

## 7. 체크리스트

### Feature 파일 작성 시

- [ ] 비즈니스 용어를 사용했는가?
- [ ] Given-When-Then이 명확히 구분되는가?
- [ ] 각 시나리오가 독립적인가?
- [ ] 파라미터로 재사용 가능하게 작성했는가?

### Step 클래스 작성 시

- [ ] `CucumberSpringConfiguration`을 상속하지 않았는가?
- [ ] `@Before`에서 `RestAssured.port` 설정을 했는가?
- [ ] API 응답을 인스턴스 변수에 저장했는가?
- [ ] `Then`에서 상태코드와 응답 본문을 모두 검증했는가?
- [ ] DB 상태도 함께 검증했는가?

### 실행 전

- [ ] `./gradlew test` 로 테스트 실행 확인
- [ ] 테스트 간 데이터 격리가 되는가?

---

## 부록: 자주 사용하는 RestAssured 패턴

```java
// GET 요청
RestAssured.given()
    .when()
        .get("/admin/reservations")
    .then()
        .extract();

// POST 요청 (JSON Body)
RestAssured.given()
    .contentType(MediaType.APPLICATION_JSON_VALUE)
    .body(Map.of("key", "value"))
.when()
    .post("/admin/products")
.then()
    .extract();

// PATCH 요청 (Path Variable)
RestAssured.given()
    .contentType(MediaType.APPLICATION_JSON_VALUE)
    .body(Map.of("status", "CANCELLED"))
.when()
    .patch("/admin/reservations/{id}/status", reservationId)
.then()
    .extract();

// Query Parameter
RestAssured.given()
    .queryParam("date", "2024-01-15")
.when()
    .get("/admin/reports/revenue/daily")
.then()
    .extract();

// 인증 헤더
RestAssured.given()
    .header("Authorization", "Bearer " + token)
.when()
    .get("/admin/reservations")
.then()
    .extract();

// 응답 검증
assertThat(response.statusCode()).isEqualTo(200);
assertThat(response.jsonPath().getString("status")).isEqualTo("CANCELLED");
assertThat(response.jsonPath().getList("$")).hasSize(3);
```