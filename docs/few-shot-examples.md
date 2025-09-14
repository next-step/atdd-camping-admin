# 🎯 Few-Shot 예제

## 📋 예제 개요

실제 프로젝트의 구현된 코드를 기반으로 한 Few-Shot 예제들입니다. 새로운 기능을 개발할 때 이 예제들을 참고하여 일관된 패턴으로 구현하세요.

---

## 🥒 Gherkin 시나리오 작성 예제

### ✅ 좋은 예제 - rental.feature

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

  Scenario: 존재하지 않는 상품에 대해, 관리자는 대여 기록을 작성할 수 없다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 999 |  # 존재하지 않는 상품 ID
      | quantity      | 1 |
    Then 대여에 실패한다.
    And 대여 기록이 생성되지 않는다.
      | productId | 999 |
```

### 📝 패턴 분석

- **한글 시나리오**: 비즈니스 담당자가 이해하기 쉬움
- **DataTable 활용**: 파라미터를 구조화하여 표현
- **비즈니스 규칙 포함**: 성공 케이스와 예외 케이스 모두 커버
- **구체적인 데이터**: 추상적이지 않고 실제 사용할 데이터 명시

---

## 📝 Cucumber Steps 구현 예제

### ✅ 좋은 예제 - RentalSteps.java

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
        
        // 추가 검증: 실제 대여 기록이 DB에 저장되었는지 확인
        List<Map<String, Object>> rentals = RentalTestFixture.대여_기록_목록_조회().jsonPath().getList("$");
        assertThat(rentals).isNotEmpty();
    }

    @And("상품의 수량이 대여 기록의 수량 만큼 줄어든다.")
    public void 상품의_수량이_대여_기록의_수량_만큼_줄어든다() {
        // 초기 재고량(20)보다 작은지 확인 (대여량 2개 차감)
        Map<String, Object> product = ProductTestFixture.특정_상품_조회(1);
        Integer currentStock = (Integer) product.get("stockQuantity");
        assertThat(currentStock).isLessThan(20);
        assertThat(currentStock).isEqualTo(18); // 20 - 2 = 18
    }

    @Then("대여에 실패한다.")
    public void 대여에_실패한다() {
        assertThat(response.statusCode()).isIn(400, 404, 409); // 비즈니스 에러 상태 코드들
    }

    @And("대여 기록이 생성되지 않는다.")
    public void 대여_기록이_생성되지_않는다(DataTable dataTable) {
        Map<String, Integer> params = dataTable.asMap(String.class, Integer.class);
        int productId = params.get("productId");
        
        // 해당 상품에 대한 대여 기록이 없는지 확인
        List<Map<String, Object>> rentals = RentalTestFixture.대여_기록_목록_조회().jsonPath().getList("$");
        boolean hasRentalForProduct = rentals.stream()
                .anyMatch(rental -> ((Integer) rental.get("productId")) == productId);
        
        assertThat(hasRentalForProduct).isFalse();
    }
}
```

### 📝 패턴 분석

- **DataTable → Map 변환**: `dataTable.asMap(String.class, Integer.class)`
- **TestFixture 위임**: 실제 API 호출은 TestFixture에서 처리
- **StepContext 활용**: 스텝 간 데이터 공유 (`StepContext.setResponse()`)
- **적절한 Assertion**: 단순한 상태 코드 검증을 넘어 비즈니스 로직 검증
- **구체적인 검증**: 추상적이지 않고 실제 값까지 확인

---

## 🛠️ TestFixture 구현 예제

### ✅ 좋은 예제 - RentalTestFixture.java

```java
import static com.camping.admin.helper.ApiHelper.createExtractableResponseWithAuthorization;
import static org.assertj.core.api.Assertions.assertThat;

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

### ✅ 좋은 예제 - ReservationTestFixture.java

```java
import static com.camping.admin.helper.ApiHelper.createExtractableResponseWithAuthorization;

public class ReservationTestFixture {
    
    public static ExtractableResponse<Response> 예약_상태_변경(long reservationId, Map<String, String> body) {
        return createExtractableResponseWithAuthorization("PATCH", "/admin/reservations/" + reservationId + "/status", body);
    }

    public static ExtractableResponse<Response> 예약_목록_조회() {
        ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("GET", "/admin/reservations");
        if (response.statusCode() != 200) {
            throw new AssertionError("예약 목록 조회 실패: " + response.statusCode());
        }
        return response;
    }

    public static Map<String, Object> 특정_예약_조회(long reservationId) {
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> ((Number) r.get("id")).longValue() == reservationId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다."));
    }

    public static Map<String, Object> 예약상태가_CONFIRMED인_특정예약조회(String reservationStatus) {
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(s -> s.get("status").equals(reservationStatus))
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다."));
    }
}
```

### 📝 패턴 분석

- **Static Import 활용**: `createExtractableResponseWithAuthorization` 간결하게 사용
- **적절한 추상화**: 반복되는 로직을 메서드로 분리
- **기본 검증 포함**: 기본적인 성공 케이스는 메서드 내에서 검증
- **유틸리티 메서드**: 특정 조건의 데이터를 찾는 헬퍼 메서드 제공
- **예외 처리**: 데이터를 찾지 못했을 때 명확한 에러 메시지

---

## 📄 HTTP 파일 작성 예제

### ✅ 좋은 예제 - rental-admin.http

```http
### 로그인 (토큰 획득)
POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

> {%
client.global.set("authToken", response.body.accessToken);
%}

### 대여 기록 생성 - 성공 케이스
POST http://localhost:8081/admin/rentals
Authorization: Bearer {{authToken}}
Content-Type: application/json

{
  "reservationId": 1,
  "productId": 1,
  "quantity": 2
}

### 대여 기록 생성 - 실패 케이스 (SALE 상품)
POST http://localhost:8081/admin/rentals
Authorization: Bearer {{authToken}}
Content-Type: application/json

{
  "reservationId": 1,
  "productId": 2,
  "quantity": 1
}

### 대여 목록 조회
GET http://localhost:8081/admin/rentals
Authorization: Bearer {{authToken}}
```

### ✅ 좋은 예제 - reservation-admin.http

```http
### 예약 목록 조회
GET http://localhost:8081/admin/reservations
Authorization: Bearer {{authToken}}

### 예약 상태 변경 - CONFIRMED
PATCH http://localhost:8081/admin/reservations/1/status
Authorization: Bearer {{authToken}}
Content-Type: application/json

{
  "status": "CONFIRMED"
}

### 예약 상태 변경 - CANCELLED
PATCH http://localhost:8081/admin/reservations/2/status
Authorization: Bearer {{authToken}}
Content-Type: application/json

{
  "status": "CANCELLED"
}
```

### 📝 패턴 분석

- **토큰 자동화**: 로그인 후 토큰을 전역 변수로 저장
- **성공/실패 케이스 모두 포함**: 다양한 시나리오 테스트 가능
- **명확한 주석**: 각 요청의 목적을 설명
- **실제 데이터**: 초기 데이터(data.sql)에 있는 실제 ID 사용

---

## 🔧 Helper 시스템 활용 예제

### ✅ ApiHelper 사용 패턴

```java
// ❌ 이렇게 하지 마세요
RestAssured.given()
    .spec(StepContext.getRequestSpecification())
    .header("Authorization", "Bearer " + StepContext.getAccessToken())
    .body(body)
    .when()
    .post("/admin/rentals")
    .then()
    .extract();

// ✅ 이렇게 하세요
createExtractableResponseWithAuthorization("POST", "/admin/rentals", body);
```

### ✅ 다양한 사용 예제

```java
// 인증이 필요한 GET 요청 (body 없음)
ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("GET", "/admin/products");

// 인증이 필요한 POST 요청 (body 있음)
Map<String, Integer> requestBody = Map.of("productId", 1, "quantity", 2);
ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("POST", "/admin/rentals", requestBody);

// 동적 URL 구성
String url = "/admin/reservations/" + reservationId + "/status";
Map<String, String> statusUpdate = Map.of("status", "CONFIRMED");
ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("PATCH", url, statusUpdate);

// 인증이 필요 없는 요청 (거의 사용하지 않음)
ExtractableResponse<Response> response = createExtractableResponse("GET", "/health");
```

---

## 🎯 실제 프로젝트 구조 예제

### 현재 프로젝트의 파일 구조

```
📁 대여(Rental) 기능 구현 예제
├── 📄 http/rental-admin.http              # 1단계: HTTP 파일
├── 📄 src/test/resources/features/rental.feature  # 2단계: Gherkin 시나리오  
├── 📄 src/test/java/com/camping/admin/steps/
│   ├── RentalSteps.java                   # 3단계: Cucumber Steps
│   └── RentalTestFixture.java             # 4단계: TestFixture
```

### 기능별 구현 패턴

```java
// 📋 1. 조회 기능 패턴
public static ExtractableResponse<Response> {도메인}_목록_조회() {
    ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("GET", "/{endpoint}");
    assertThat(response.statusCode()).isEqualTo(200);
    return response;
}

// 📝 2. 생성 기능 패턴  
public static ExtractableResponse<Response> {도메인}_생성_요청(Map<String, Object> body) {
    return createExtractableResponseWithAuthorization("POST", "/{endpoint}", body);
}

// 🔍 3. 특정 항목 조회 패턴
public static Map<String, Object> 특정_{도메인}_조회(long id) {
    return {도메인}_목록_조회().jsonPath()
            .<Map<String, Object>>getList("$").stream()
            .filter(item -> ((Number) item.get("id")).longValue() == id)
            .findFirst()
            .orElseThrow(() -> new AssertionError("{도메인}을 찾을 수 없습니다."));
}

// 🔄 4. 업데이트 기능 패턴
public static ExtractableResponse<Response> {도메인}_업데이트_요청(long id, Map<String, String> body) {
    return createExtractableResponseWithAuthorization("PATCH", "/{endpoint}/" + id, body);
}
```

---

## 💬 커밋 메시지 예제

### ✅ 단계별 커밋 예제

#### 1단계: HTTP 파일 추가

```bash
git commit -m "feat: add rental admin HTTP requests for testing

- Add POST /admin/rentals endpoint test
- Add GET /admin/rentals endpoint test  
- Include success and failure scenarios
- Add authorization headers for admin access

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

#### 2단계: Gherkin 시나리오 추가

```bash
git commit -m "test: add rental creation scenarios

- Add scenario for successful rental record creation
- Add scenario for non-rental product failure case
- Add scenario for non-existent product failure case
- Include DataTable for structured test parameters
- Cover business rules validation

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com)"
```

#### 3단계: Cucumber Steps 구현

```bash
git commit -m "test: implement rental Cucumber steps

- Add step definitions for rental record creation
- Add validation steps for business rules
- Integrate with RentalTestFixture for API calls
- Include proper error handling and assertions
- Add stock quantity validation logic

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com)"
```

#### 4단계: TestFixture 구현

```bash  
git commit -m "test: add RentalTestFixture for API abstraction

- Add rental record creation helper method
- Add rental record listing with validation
- Add specific rental record finder utility
- Integrate with ApiHelper for clean HTTP requests
- Include proper error handling and assertions

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com)"
```

#### 5단계: 최종 통합 커밋

```bash
git commit -m "feat: implement rental record management with ATDD approach

Complete implementation of rental record functionality following ATDD methodology:

📋 Implementation Steps:
- ✅ HTTP request templates for manual testing
- ✅ Gherkin scenarios covering all business rules  
- ✅ Cucumber step definitions with comprehensive validation
- ✅ TestFixture for API call abstraction
- ✅ Integration with existing helper system

🎯 Business Rules Covered:
- ✅ RENTAL type products can be rented with quantity validation
- ✅ SALE type products cannot be rented (proper error handling)
- ✅ Non-existent products return appropriate error responses
- ✅ Stock quantity decreases correctly after successful rental
- ✅ All edge cases and error scenarios included

🛠️ Technical Implementation:
- Uses ApiHelper for consistent HTTP request handling
- Follows established TestFixture patterns for maintainability
- Includes proper data validation and business rule enforcement
- Integrates seamlessly with existing test infrastructure

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com)"
```

### 📝 커밋 메시지 패턴 분석

- **명확한 타입**: `feat:`, `test:` 등으로 변경 유형 표시
- **구체적인 설명**: 어떤 기능을 구현했는지 명확히 표시
- **체크리스트 형태**: 구현된 내용을 목록으로 정리
- **비즈니스 규칙 포함**: 기술적 구현뿐만 아니라 비즈니스 로직도 설명
- **일관된 시그니처**: 모든 커밋에 Claude Code 시그니처 포함

이러한 패턴들을 참고하여 새로운 기능을 개발할 때 일관성을 유지하세요! 🎯