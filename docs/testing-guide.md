# 🧪 테스트 개발 가이드

## 🎯 ATDD 개발 프로세스

우리 프로젝트는 **Acceptance Test Driven Development** 방식을 따릅니다.

### 📋 전체 프로세스 개요

```
1️⃣ HTTP 파일 작성 → 2️⃣ Gherkin 시나리오 → 3️⃣ Cucumber Steps → 4️⃣ TestFixture → 5️⃣ 커밋
```

---

## 1️⃣ HTTP 파일 작성

### 목적

- API 스펙 확인 및 수동 테스트
- 실제 요청/응답 형태 파악

### 위치

```
http/
├── auth.http           # 인증 관련
├── rental-admin.http   # 대여 관리  
├── reservation-admin.http  # 예약 관리
└── ...
```

### 작성 방법

```http
### 대여 기록 생성
POST http://localhost:8081/admin/rentals
Authorization: Bearer {{authToken}}
Content-Type: application/json

{
  "reservationId": 1,
  "productId": 1,
  "quantity": 2
}

### 대여 목록 조회
GET http://localhost:8081/admin/rentals
Authorization: Bearer {{authToken}}
```

### ✅ 커밋 시점

```bash
# HTTP 파일 작성 완료 후
git add http/rental-admin.http
git commit -m "feat: add rental admin HTTP requests for testing

- Add POST /admin/rentals endpoint test
- Add GET /admin/rentals endpoint test
- Include authorization headers for admin access

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

---

## 2️⃣ Gherkin 시나리오 작성

### 목적

- 비즈니스 요구사항을 자연어로 표현
- 테스트 시나리오 명확히 정의

### 위치

```
src/test/resources/features/
├── rental.feature      # 대여 관련 시나리오
├── reservation.feature # 예약 관련 시나리오
└── ...
```

### 작성 방법

```gherkin
Feature: 대여 생성

  Scenario: 상품이 등록되어 있고 렌탈 상품이라면, 관리자는 특정 상품의 수량 만큼 대여 기록을 작성할 수 있다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 1 |
      | quantity      | 2 |
    Then 대여 기록이 생성된다.
    And 상품의 수량이 대여 기록의 수량 만큼 줄어든다.

  Scenario: 상품이 렌탈 상품이 아니라면, 관리자는 대여 기록을 작성할 수 없다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 2 |  # SALE 타입 상품
      | quantity      | 1 |
    Then 대여에 실패한다.
    And 대여 기록이 생성되지 않는다.
      | productId | 2 |
```

### 📝 작성 규칙

- **한글 시나리오명**: 비즈니스 담당자가 이해하기 쉽게
- **구체적인 데이터**: DataTable 활용으로 명확한 테스트 조건
- **비즈니스 규칙 포함**: 예외 상황도 반드시 포함

### ✅ 커밋 시점

```bash
# Gherkin 시나리오 작성 완료 후
git add src/test/resources/features/rental.feature
git commit -m "test: add rental creation scenarios

- Add scenario for successful rental record creation
- Add scenario for non-rental product failure case
- Add scenario for non-existent product failure case
- Include validation for business rules

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

---

## 3️⃣ Cucumber Steps 구현

### 목적

- Gherkin 시나리오를 실행 가능한 코드로 변환
- TestFixture와 연결하여 실제 API 호출

### 위치

```
src/test/java/com/camping/admin/steps/
├── RentalSteps.java      # 대여 관련 스텝
├── ReservationSteps.java # 예약 관련 스텝
└── ...
```

### 작성 방법

```java
@Component
public class RentalSteps {
    private ExtractableResponse<Response> response;

    @When("관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.")
    public void 관리자는_특정_상품의_수량_만큼_대여_기록을_작성한다(DataTable dataTable) {
        Map<String, Integer> params = dataTable.asMap(String.class, Integer.class);
        
        response = RentalTestFixture.대여_기록_작성_요청(params);
        StepContext.setResponse(response);
    }

    @Then("대여 기록이 생성된다.")
    public void 대여_기록이_생성된다() {
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @And("상품의 수량이 대여 기록의 수량 만큼 줄어든다.")
    public void 상품의_수량이_대여_기록의_수량_만큼_줄어든다() {
        // 상품 조회하여 수량 검증 로직
        Map<String, Object> product = ProductTestFixture.특정_상품_조회(1);
        assertThat((Integer) product.get("stockQuantity")).isLessThan(20);
    }
}
```

### 📝 작성 규칙

- **한글 메서드명**: Gherkin과 1:1 대응
- **DataTable 활용**: 파라미터가 많을 때 Map으로 변환
- **TestFixture 위임**: 실제 API 호출은 TestFixture에서 처리
- **StepContext 활용**: 스텝 간 데이터 공유

### ✅ 커밋 시점

```bash
# Cucumber Steps 구현 완료 후
git add src/test/java/com/camping/admin/steps/RentalSteps.java
git commit -m "test: implement rental Cucumber steps

- Add step definitions for rental record creation
- Add validation steps for business rules
- Integrate with RentalTestFixture for API calls
- Include proper error handling and assertions

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

---

## 4️⃣ TestFixture 구현

### 목적

- API 호출 로직을 재사용 가능한 형태로 추상화
- Helper 시스템을 활용한 깔끔한 코드 작성

### 위치

```
src/test/java/com/camping/admin/steps/
├── RentalTestFixture.java      # 대여 관련 픽스처
├── ReservationTestFixture.java # 예약 관련 픽스처
└── ...
```

### 작성 방법

```java
import static com.camping.admin.helper.ApiHelper.createExtractableResponseWithAuthorization;

public class RentalTestFixture {
    
    public static ExtractableResponse<Response> 대여_기록_작성_요청(Map<String, Integer> body) {
        return createExtractableResponseWithAuthorization("POST", "/admin/rentals", body);
    }

    public static ExtractableResponse<Response> 대여_기록_목록_조회() {
        ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("GET", "/admin/rentals");
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static Map<String, Object> 특정_대여_기록_조회(long rentalId) {
        return 대여_기록_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> ((Integer) r.get("id")) == rentalId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("대여 기록을 찾을 수 없습니다."));
    }
}
```

### 📝 작성 규칙

- **한글 메서드명**: 의도가 명확하게 드러나도록
- **ApiHelper 적극 활용**: HTTP 요청 단순화
- **적절한 Assertion**: 기본적인 성공 케이스는 미리 검증
- **도메인별 유틸리티**: 특정 데이터 조회/검증 메서드 제공

### ✅ 커밋 시점

```bash
# TestFixture 구현 완료 후
git add src/test/java/com/camping/admin/steps/RentalTestFixture.java
git commit -m "test: add RentalTestFixture for API abstraction

- Add rental record creation helper method
- Add rental record listing with validation
- Add specific rental record finder utility
- Integrate with ApiHelper for clean HTTP requests

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

---

## 🔧 테스트 환경 설정

### Hooks 시스템

```java
public class Hooks {
    @BeforeAll
    public static void initAccessToken() {
        // JWT 토큰 획득 및 전역 설정
        Map<String, String> params = Map.of("username", "admin", "password", "admin123");
        ExtractableResponse<Response> response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(params)
                .when()
                .post("http://localhost:8081/auth/login")
                .then()
                .statusCode(200)
                .extract();

        String accessToken = response.jsonPath().get("accessToken");
        StepContext.setAccessToken(accessToken);
    }

    @Before  
    public void beforeScenario() {
        StepContext.setSpec();
    }
}
```

### StepContext 활용

```java
public class StepContext {
    private static String accessToken;
    private static RequestSpecification requestSpecification;
    private static ExtractableResponse<Response> response;
    
    public static RequestSpecification getRequestSpecificationWithAccessToken() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + accessToken)
                .setBaseUri("http://localhost:8081")
                .build();
    }
    
    // Getter/Setter 메서드들...
}
```

## 🚀 실행 방법

### Gradle Tasks

```bash
# 전체 테스트 실행
./gradlew test

# Cucumber 테스트만 실행  
./gradlew cucumberTest

# 데이터 롤백과 함께 실행
./gradlew cucumberTestWithRollback
```

### 개발 중 테스트

1. **Spring 애플리케이션 실행**: `./gradlew bootRun`
2. **HTTP 파일로 수동 테스트**: IntelliJ HTTP Client 사용
3. **Cucumber 테스트 실행**: IDE 또는 Gradle

## ✅ 최종 통합 커밋

모든 단계가 완료되면 통합 커밋을 작성합니다:

```bash
git add .
git commit -m "feat: implement rental record management with ATDD approach

- Add HTTP request templates for manual testing
- Implement Gherkin scenarios covering business rules
- Create Cucumber step definitions with proper validation  
- Build TestFixture for API call abstraction
- Integrate with existing helper system for maintainability

Business Rules Covered:
- ✅ Rental products can be rented with quantity validation
- ✅ Sale products cannot be rented (proper error handling)
- ✅ Non-existent products return appropriate error
- ✅ Stock quantity decreases after successful rental

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

이 가이드를 따르면 일관된 품질의 ATDD 테스트를 작성할 수 있습니다! 🎯