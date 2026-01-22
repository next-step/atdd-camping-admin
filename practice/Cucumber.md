# Cucumber 완전 정복 가이드

## 1. 먼저 비유로 이해하기

### 식당 주문서로 비유

**RestAssured만 사용하는 경우** = 주방장이 직접 메모한 레시피
```
POST /api/orders
body: {"menu": "김치찌개", "quantity": 1}
assert status == 201
assert response.body.orderId != null
```
→ 개발자는 이해하지만, 사장님이나 서빙 직원은 이게 뭔지 모름

**Cucumber 사용하는 경우** = 손님도 읽을 수 있는 주문서
```gherkin
Given 손님이 테이블에 앉아있다
When 손님이 김치찌개 1개를 주문하면
Then 주문이 접수된다
```
→ 개발자, 기획자, 사장님 모두가 이해할 수 있음

---

## 2. RestAssured vs Cucumber 비교

| 구분 | RestAssured | Cucumber |
|------|-------------|----------|
| **정체** | HTTP 클라이언트 라이브러리 | BDD 테스트 프레임워크 |
| **역할** | API를 실제로 호출하는 도구 | 테스트 시나리오를 작성하는 도구 |
| **문법** | Java 코드 | Gherkin (자연어) |
| **읽는 사람** | 개발자 | 개발자 + 기획자 + QA + 누구나 |
| **관계** | 실행 도구 | 명세 도구 |

### 핵심 포인트
> **Cucumber는 RestAssured의 대체재가 아니다!**
> 오히려 **함께 사용**하는 경우가 많다.

```
[Cucumber Feature 파일] → "무엇을" 테스트할지 정의 (비즈니스 언어)
        ↓
[Step Definition] → Feature와 코드를 연결
        ↓
[RestAssured] → "어떻게" API를 호출할지 구현 (기술 언어)
```

---

## 3. 실제 코드로 비교

### RestAssured만 사용한 테스트
```java
@Test
void 관리자가_예약을_취소한다() {
    // 예약 생성
    var reservationId = given()
        .contentType(ContentType.JSON)
        .body(Map.of("userId", 1, "campingSiteId", 1, "date", "2024-01-15"))
    .when()
        .post("/api/reservations")
    .then()
        .statusCode(201)
        .extract().jsonPath().getLong("id");

    // 예약 취소
    given()
        .header("Authorization", "Bearer " + adminToken)
    .when()
        .delete("/api/reservations/" + reservationId)
    .then()
        .statusCode(200);

    // 취소 확인
    given()
    .when()
        .get("/api/reservations/" + reservationId)
    .then()
        .statusCode(200)
        .body("status", equalTo("CANCELLED"));
}
```
→ 동작은 하지만, 비즈니스 의도를 파악하려면 코드를 읽어야 함

### Cucumber + RestAssured 조합
**Feature 파일** (누구나 읽을 수 있음)
```gherkin
Feature: 관리자의 예약 취소 기능

  Scenario: 사용자가 예약한 건을 관리자가 취소하면 성공한다
    Given 사용자가 캠핑장 예약을 했다
    When 관리자가 해당 예약을 취소하면
    Then 예약이 성공적으로 취소된다
```

**Step Definition** (개발자가 구현)
```java
public class ReservationSteps {
    private Long reservationId;

    @Given("사용자가 캠핑장 예약을 했다")
    public void createReservation() {
        reservationId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("userId", 1, "campingSiteId", 1))
        .when()
            .post("/api/reservations")
        .then()
            .extract().jsonPath().getLong("id");
    }

    @When("관리자가 해당 예약을 취소하면")
    public void adminCancelsReservation() {
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/api/reservations/" + reservationId);
    }

    @Then("예약이 성공적으로 취소된다")
    public void verifyReservationCancelled() {
        given()
        .when()
            .get("/api/reservations/" + reservationId)
        .then()
            .body("status", equalTo("CANCELLED"));
    }
}
```

---

## 4. Cucumber를 왜 쓰는가?

### 장점 1: 소통의 도구
```
기획자: "이 시나리오 맞죠?"
개발자: "네, 이 feature 파일 그대로 테스트됩니다"
QA: "이 시나리오 통과했어요"
```
→ 모두가 같은 문서를 보고 이야기할 수 있음

### 장점 2: 살아있는 문서 (Living Documentation)
- 일반 문서는 코드와 따로 놀기 쉬움
- Feature 파일은 테스트가 통과해야 하므로 항상 최신 상태 유지

### 장점 3: 요구사항 = 테스트
```gherkin
# 이 feature 파일은 동시에:
# 1. 요구사항 명세서
# 2. 인수 테스트 케이스
# 3. 기능 문서
```

### 장점 4: Step 재사용
```gherkin
Scenario: 관리자가 예약 취소
  Given 사용자가 캠핑장 예약을 했다    # 재사용 가능!
  When 관리자가 해당 예약을 취소하면
  Then 예약이 성공적으로 취소된다

Scenario: 사용자가 예약 조회
  Given 사용자가 캠핑장 예약을 했다    # 같은 Step 재사용!
  When 사용자가 예약 목록을 조회하면
  Then 예약 정보가 표시된다
```

---

## 5. 그래서 언제 뭘 쓰나?

### RestAssured만 쓰는 경우
- 팀이 개발자로만 구성됨
- 빠르게 API 테스트만 작성하면 됨
- 비즈니스 시나리오보다 기술적 검증이 목적

### Cucumber를 도입하는 경우
- 기획자/QA와 테스트 시나리오를 공유해야 함
- 인수 테스트(Acceptance Test)를 명확히 정의하고 싶음
- 요구사항 문서와 테스트를 일치시키고 싶음

### 현실적인 조합
```
Cucumber (시나리오 정의)
    +
RestAssured (API 호출 구현)
    +
Spring Boot Test (테스트 환경 구성)
```

---

## 6. 파일 구조 이해하기

```
src/test/
├── java/com/camping/admin/
│   ├── CucumberTestRunner.java    # Cucumber 실행 설정
│   └── steps/
│       └── ReservationSteps.java  # Step ↔ 코드 연결
└── resources/
    └── features/
        └── reservation_cancel.feature  # 시나리오 정의
```

### 실행 흐름
```
1. CucumberTestRunner 실행
       ↓
2. features/ 폴더에서 .feature 파일 읽음
       ↓
3. 각 Step 텍스트와 매칭되는 @Given/@When/@Then 메서드 찾음
       ↓
4. 메서드 실행 (여기서 RestAssured 등으로 실제 API 호출)
       ↓
5. 결과 리포트 생성
```

---

## 7. CucumberTestRunner 이해하기

### 코드 분석
```java
@Suite
@SelectClasspathResource("features")  // 어디서 feature 파일 찾을지
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.camping.admin.steps")  // 어디서 Step Definition 찾을지
public class CucumberTestRunner {
    // 내용 없음! 설정만 담당
}
```

### 역할: 지휘자
```
CucumberTestRunner = 지휘자

지휘자가 하는 일:
1. 악보(feature 파일) 가져옴
2. 연주자(Step Definition) 모음
3. 악보 보고 연주자한테 "너 이거 연주해!" 지시
4. 결과 보고
```

### 실행 흐름 상세
```
CucumberTestRunner 실행
        ↓
@SelectClasspathResource("features")
→ "src/test/resources/features/" 폴더에서 .feature 파일 찾음
        ↓
@ConfigurationParameter(GLUE_PROPERTY_NAME, "com.camping.admin.steps")
→ "com.camping.admin.steps" 패키지에서 @Given/@When/@Then 붙은 메서드 찾음
        ↓
Feature의 각 Step 텍스트 ↔ 어노테이션 문자열 매칭
        ↓
매칭된 메서드 순서대로 실행
        ↓
결과 리포트 생성
```

### 왜 클래스 내용이 비어있어도 될까?
- 실제 로직은 **Cucumber 프레임워크**가 처리
- 이 클래스는 **설정(Configuration)만 담당**
- 어노테이션이 모든 설정 정보를 가지고 있음

---

## 8. Feature 파일만 vs Cucumber 연동

### 흔한 오해
> "저번에 .feature 파일 작성했는데, 그것도 Cucumber 아니야?"

### 답변: Feature 파일 ≠ Cucumber 테스트

| 구분 | Feature 파일만 | Feature + Step Definition + Runner |
|------|----------------|-------------------------------------|
| **상태** | 그냥 문서 | 실행되는 테스트 |
| **검증** | 사람이 눈으로 읽음 | 자동으로 실행됨 |
| **가치** | 요구사항 정리 | 요구사항 + 자동 검증 |

### 비유: 악보와 연주
```
Feature 파일만 = 악보만 있음 (연주 안 함)
Cucumber 연동 = 악보 + 연주자 + 지휘자 → 실제 음악이 연주됨
```

### 연동에 필요한 3가지
```
1. Feature 파일 (.feature)     → 시나리오 정의
2. Step Definition (.java)     → 각 Step의 실제 동작 구현
3. CucumberTestRunner (.java)  → 둘을 연결하고 실행
```

### 실제 예시
```
[reservation_cancel.feature]
Given 사용자가 캠핑장 예약을 했다
        ↓ 텍스트 매칭
[ReservationSteps.java]
@Given("사용자가 캠핑장 예약을 했다")
public void createReservation() { ... }
        ↓ 실행 지시
[CucumberTestRunner.java]
@SelectClasspathResource("features")
@ConfigurationParameter(GLUE, "com.camping.admin.steps")
```

---

## 9. 핵심 정리

| 질문 | 답변 |
|------|------|
| Cucumber가 뭐야? | 자연어로 테스트 시나리오를 작성하는 BDD 프레임워크 |
| RestAssured랑 뭐가 달라? | Cucumber는 "무엇을", RestAssured는 "어떻게" |
| 왜 써? | 개발자가 아닌 사람도 테스트를 이해할 수 있게 |
| 둘 중 하나만 써야 해? | 아니, 보통 함께 사용함 |

---

## 10. 한 줄 요약

> **Cucumber = 모두가 이해할 수 있는 언어로 테스트 시나리오를 작성하고,
> 그 시나리오가 실제로 동작하는지 검증하는 도구**
