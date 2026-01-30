# Cucumber Hook vs Background 비교

## 실행 순서

```
┌─────────────────────────────────────────────────────────────┐
│                      Feature 파일                            │
├─────────────────────────────────────────────────────────────┤
│  Scenario 1                                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 1. @Before (Hook)     ← 모든 시나리오 전에 실행        │   │
│  │ 2. Background         ← Feature에 정의된 공통 Given   │   │
│  │ 3. Given / When / Then                              │   │
│  │ 4. @After (Hook)      ← 모든 시나리오 후에 실행        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  Scenario 2                                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 1. @Before (Hook)     ← 다시 실행                     │   │
│  │ 2. Background         ← 다시 실행                     │   │
│  │ 3. Given / When / Then                              │   │
│  │ 4. @After (Hook)      ← 다시 실행                     │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 예시 Feature 파일

```gherkin
Feature: 대여 생성 기능

  Background:
    Given 관리자로 로그인되어 있다

  Scenario: 예약 고객에게 장비를 대여한다
    Given "텐트" 대여 상품이 재고 10개로 등록되어 있다
    When 관리자가 예약 없이 "텐트" 상품 1개를 대여한다
    Then 대여가 정상적으로 생성된다

  Scenario: 재고가 부족한 상품을 대여 시도한다
    Given "랜턴" 대여 상품이 재고 2개로 등록되어 있다
    When 관리자가 예약 없이 "랜턴" 상품 5개를 대여한다
    Then 재고가 부족하다는 오류가 발생한다
```

## 예시 Hook 코드

```java
// CucumberSpringConfiguration.java
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Before  // ← Cucumber Hook (io.cucumber.java.Before)
    public void setUp() {
        System.out.println("1. [Hook] @Before 실행 - DB 초기화");
        databaseCleaner.clear();
    }

    @After   // ← Cucumber Hook (io.cucumber.java.After)
    public void tearDown() {
        System.out.println("4. [Hook] @After 실행");
    }
}
```

## 예시 Step 코드

```java
// AuthSteps.java
public class AuthSteps {

    @Given("관리자로 로그인되어 있다")  // ← Background에서 호출됨
    public void 관리자_로그인() {
        System.out.println("2. [Background] 관리자 로그인");
        // 로그인 로직
    }
}

// RentalSteps.java
public class RentalSteps {

    @Given("{string} 대여 상품이 재고 {int}개로 등록되어 있다")
    public void 대여_상품이_등록되어_있다(String productName, int stockQuantity) {
        System.out.println("3. [Scenario] Given 실행 - " + productName);
        // 상품 생성 로직
    }
}
```

## 실행 결과 (콘솔 출력)

```
========== Scenario 1: 예약 고객에게 장비를 대여한다 ==========
1. [Hook] @Before 실행 - DB 초기화
2. [Background] 관리자 로그인
3. [Scenario] Given 실행 - 텐트
   ... When, Then 실행 ...
4. [Hook] @After 실행

========== Scenario 2: 재고가 부족한 상품을 대여 시도한다 ==========
1. [Hook] @Before 실행 - DB 초기화    ← 다시 실행됨!
2. [Background] 관리자 로그인          ← 다시 실행됨!
3. [Scenario] Given 실행 - 랜턴
   ... When, Then 실행 ...
4. [Hook] @After 실행
```

## Hook vs Background 비교

| 구분 | Hook (@Before/@After) | Background |
|------|----------------------|------------|
| **정의 위치** | Java 코드 | Feature 파일 (Gherkin) |
| **실행 시점** | 시나리오 전/후 | Given 스텝 전 |
| **용도** | 기술적 설정 (DB 초기화, 브라우저 설정 등) | 비즈니스 사전조건 (로그인, 데이터 준비 등) |
| **가시성** | 개발자만 볼 수 있음 | Feature 파일에서 누구나 볼 수 있음 |
| **재사용** | 모든 Feature에 자동 적용 | 해당 Feature 내에서만 적용 |

## 언제 무엇을 사용해야 하나?

### Hook 사용 (기술적 설정)
```java
@Before
public void setUp() {
    databaseCleaner.clear();     // DB 초기화
    RestAssured.port = port;      // 테스트 서버 포트 설정
}
```

### Background 사용 (비즈니스 사전조건)
```gherkin
Background:
  Given 관리자로 로그인되어 있다
  And 기본 캠프사이트가 등록되어 있다
```

## 주의: @Before 어노테이션 구분

```java
import io.cucumber.java.Before;      // ← Cucumber Hook (시나리오마다 실행)
import org.junit.jupiter.api.BeforeEach;  // ← JUnit (테스트 클래스마다 실행)
```

**Cucumber에서는 `io.cucumber.java.Before`를 사용해야 합니다!**

---

## Hook은 어디에 정의해도 글로벌하게 적용된다

### 중요한 특성

Cucumber의 `@Before`/`@After` Hook은 **어떤 클래스에 정의하든 모든 Feature의 모든 시나리오에 적용**됩니다.

### 예시

```
features/
├── rental_create.feature       # 대여 관련 시나리오
├── reservation_cancel.feature  # 예약 관련 시나리오
└── product_manage.feature      # 상품 관련 시나리오
```

```java
// RentalSteps.java 안에 Hook 정의
@Before
public void setUp() {
    System.out.println("RentalSteps @Before 실행");
}
```

### 실행 결과

```
rental_create.feature - Scenario 1
  → "RentalSteps @Before 실행" ✅

rental_create.feature - Scenario 2
  → "RentalSteps @Before 실행" ✅

reservation_cancel.feature - Scenario 1
  → "RentalSteps @Before 실행" ✅  ← 다른 Feature인데도 실행됨!

product_manage.feature - Scenario 1
  → "RentalSteps @Before 실행" ✅  ← 다른 Feature인데도 실행됨!
```

**RentalSteps.java에 정의했지만, 모든 Feature의 시나리오에서 실행됩니다.**

### 왜 이렇게 동작하나?

Cucumber는 Hook을 **클래스 단위가 아니라 글로벌하게** 수집합니다. 어떤 클래스에 정의했든 상관없이 모든 `@Before`는 모든 시나리오 전에 실행됩니다.

---

## 특정 시나리오에만 Hook 적용하기 (태그 사용)

특정 Feature나 시나리오에만 Hook을 적용하려면 **태그(@)**를 사용해야 합니다.

### Feature 파일에 태그 추가

```gherkin
@rental
Feature: 대여 생성 기능

  Scenario: 예약 고객에게 장비를 대여한다
    Given ...

@slow
Feature: 대용량 데이터 테스트

  Scenario: 10만건 데이터 처리
    Given ...
```

### 태그 기반 Hook

```java
// 모든 시나리오에 실행 (기본)
@Before
public void 모든_시나리오_전() {
    databaseCleaner.clear();
}

// @rental 태그가 붙은 시나리오에만 실행
@Before("@rental")
public void 대여_시나리오_전() {
    // 대여 관련 초기 데이터 설정
}

// @slow 태그가 붙은 시나리오에만 실행
@Before("@slow")
public void 느린_테스트_전() {
    // 타임아웃 설정 변경 등
}

// @rental이 아닌 시나리오에만 실행
@Before("not @rental")
public void 대여_아닌_시나리오_전() {
    // ...
}

// 여러 태그 조합
@Before("@rental and @admin")
public void 대여_관리자_시나리오_전() {
    // @rental과 @admin 둘 다 있는 경우만
}
```

### 태그 조건 정리

| Hook | 적용 대상 |
|------|----------|
| `@Before` | 모든 시나리오 |
| `@Before("@rental")` | @rental 태그가 붙은 시나리오만 |
| `@Before("@rental and @admin")` | 두 태그 모두 있는 시나리오만 |
| `@Before("@rental or @reservation")` | 둘 중 하나라도 있는 시나리오 |
| `@Before("not @slow")` | @slow 태그가 없는 시나리오만 |

---

## 권장 구조

```java
// CucumberSpringConfiguration.java - 공통 Hook 집중
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        databaseCleaner.clear();
    }
}

// RentalSteps.java - Step 정의만
public class RentalSteps {
    // @Before 없음 - Step 메서드만 정의

    @Given("{string} 대여 상품이 재고 {int}개로 등록되어 있다")
    public void 대여_상품이_등록되어_있다(...) { ... }
}

// ReservationSteps.java - Step 정의만
public class ReservationSteps {
    // @Before 없음 - Step 메서드만 정의

    @Given("{string} 고객의 예약이 존재한다")
    public void 예약이_존재한다(...) { ... }
}
```

**공통 Hook은 설정 클래스에 모아두고, Step 클래스는 Step 정의에만 집중하는 것이 깔끔합니다.**