# 🛠️ 헬퍼 시스템 가이드

## 🎯 헬퍼 시스템 개요

우리 프로젝트는 **전략 패턴**을 활용한 강력한 헬퍼 시스템을 제공합니다. 이를 통해 HTTP 요청 코드를 대폭 단순화하고 유지보수성을 향상시킵니다.

## 📐 시스템 구조

```
src/test/java/com/camping/admin/helper/
├── 🎯 ApiHelper.java              # 최상위 API 호출 인터페이스
├── 🔧 RestAssuredHelper.java      # RestAssured 래퍼
├── 📋 HttpMethod.java             # HTTP 메서드 enum
├── ⚡ HttpMethodStrategy.java     # 전략 패턴 인터페이스
├── 🔄 GetStrategy.java           # GET 요청 전략
├── 📝 PostStrategy.java          # POST 요청 전략  
├── ✏️ PutStrategy.java           # PUT 요청 전략
├── 🔧 PatchStrategy.java         # PATCH 요청 전략
└── 🗑️ DeleteStrategy.java        # DELETE 요청 전략
```

---

## 🎯 ApiHelper - 최상위 인터페이스

### 기본 사용법

```java
import static com.camping.admin.helper.ApiHelper.*;

// 인증 없는 요청
ExtractableResponse response = createExtractableResponse("GET", "/admin/products");

// 인증 필요한 요청  
ExtractableResponse response = createExtractableResponseWithAuthorization("POST", "/admin/rentals", body);

// body 없는 요청
ExtractableResponse response = createExtractableResponseWithAuthorization("GET", "/admin/rentals");
```

### 제공되는 메서드

```java
public class ApiHelper {
    // 기본 요청 (인증 없음)
    public static <T> ExtractableResponse createExtractableResponse(String httpMethod, String url, T body)
    public static ExtractableResponse createExtractableResponse(String httpMethod, String url)
    
    // 인증 필요 요청
    public static <T> ExtractableResponse createExtractableResponseWithAuthorization(String httpMethod, String url, T body)  
    public static ExtractableResponse createExtractableResponseWithAuthorization(String httpMethod, String url)
    
    // 세밀한 제어 (직접 사용 권장하지 않음)
    public static <T> ExtractableResponse createExtractableResponse(String httpMethod, String url, T body, boolean needAuthorization)
}
```

### 💡 사용 팁

- **정적 import 활용**: `import static com.camping.admin.helper.ApiHelper.*;`
- **메서드명으로 의도 표현**: `WithAuthorization`이 붙은 메서드는 JWT 토큰 자동 포함
- **타입 안전성**: 제네릭을 통해 다양한 body 타입 지원

---

## 🔧 RestAssuredHelper - 핵심 엔진

### 내부 구조

```java
public class RestAssuredHelper {
    // 전략 패턴으로 HTTP 메서드별 처리
    private static final List<HttpMethodStrategy> strategies = List.of(
        new GetStrategy(), new PostStrategy(), new PutStrategy(), 
        new PatchStrategy(), new DeleteStrategy()
    );
    
    // 통합 실행 메서드
    public <T> ExtractableResponse execute(HttpMethod method, String url, T body, boolean needAuthorization) {
        RequestSpecification requestSpec = needAuthorization 
            ? StepContext.getRequestSpecificationWithAccessToken()
            : StepContext.getRequestSpecification();
            
        // 적절한 전략 찾아서 실행
        for (HttpMethodStrategy strategy : strategies) {
            if (strategy.supports(method)) {
                return strategy.execute(requestSpec, url, body);
            }
        }
        
        throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }
}
```

### HTTP 메서드별 편의 메서드

```java
// 모든 메서드는 4가지 오버로드 제공
public <T> ExtractableResponse get(String url, T body, boolean needAuthorization)
public <T> ExtractableResponse get(String url, T body)  // needAuthorization = false
public ExtractableResponse get(String url)              // body = null, needAuthorization = false  
public ExtractableResponse get(String url, boolean needAuthorization) // body = null

// POST, PUT, PATCH, DELETE 메서드도 동일한 패턴
```

---

## ⚡ 전략 패턴 구현

### HttpMethodStrategy 인터페이스

```java
public interface HttpMethodStrategy {
    <T> ExtractableResponse execute(RequestSpecification requestSpec, String url, T body);
    boolean supports(HttpMethod method);
}
```

### 전략 구현 예시 - PostStrategy

```java
public class PostStrategy implements HttpMethodStrategy {
    
    @Override
    public <T> ExtractableResponse execute(RequestSpecification requestSpec, String url, T body) {
        RequestSpecification given = RestAssured.given().spec(requestSpec);
        if (body != null) {
            given = given.body(body);
        }
        
        return given.when()
                .post(url)
                .then()
                .extract();
    }
    
    @Override
    public boolean supports(HttpMethod method) {
        return method == HttpMethod.POST;
    }
}
```

### 전략 패턴의 장점

- ✅ **확장성**: 새로운 HTTP 메서드 추가 용이
- ✅ **단일 책임**: 각 전략이 하나의 HTTP 메서드만 담당
- ✅ **테스트 용이성**: 각 전략을 독립적으로 테스트 가능
- ✅ **유지보수성**: 특정 메서드 로직 변경 시 해당 전략만 수정

---

## 🔧 실제 사용 예제

### TestFixture에서의 활용

```java
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

### Before/After 비교

#### ❌ Helper 사용 전 (복잡하고 중복 많음)

```java
public static ExtractableResponse<Response> 예약_상태_변경(long reservationId, Map<String, String> body) {
    return RestAssured.given()
            .spec(StepContext.getRequestSpecification())
            .header("Authorization", "Bearer " + StepContext.getAccessToken())
            .body(body)
            .when()
            .patch("/admin/reservations/" + reservationId + "/status")
            .then()
            .extract();
}
```

#### ✅ Helper 사용 후 (간결하고 명확함)

```java
public static ExtractableResponse<Response> 예약_상태_변경(long reservationId, Map<String, String> body) {
    return createExtractableResponseWithAuthorization("PATCH", "/admin/reservations/" + reservationId + "/status", body);
}
```

---

## 🔍 고급 활용법

### 1. 커스텀 검증이 필요한 경우

```java
public static ExtractableResponse<Response> 대여_기록_목록_조회_성공() {
    ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("GET", "/admin/rentals");
    
    // 추가 검증 로직
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.jsonPath().getList("$")).isNotEmpty();
    
    return response;
}
```

### 2. 동적 URL 구성

```java
public static ExtractableResponse<Response> 특정_예약_상태_변경(long reservationId, String status) {
    Map<String, String> body = Map.of("status", status);
    String url = "/admin/reservations/" + reservationId + "/status";
    
    return createExtractableResponseWithAuthorization("PATCH", url, body);
}
```

### 3. 에러 케이스 테스트

```java
public static ExtractableResponse<Response> 존재하지_않는_상품_대여_시도(int productId) {
    Map<String, Integer> body = Map.of(
        "reservationId", 1,
        "productId", productId,  // 존재하지 않는 ID
        "quantity", 1
    );
    
    ExtractableResponse<Response> response = createExtractableResponseWithAuthorization("POST", "/admin/rentals", body);
    
    // 에러 상황이므로 성공 검증은 하지 않음
    return response;
}
```

---

## 🚨 주의사항

### ⚠️ 하지 말아야 할 것들

```java
// ❌ 직접 RestAssured 사용 (Helper를 우회)
RestAssured.given()
    .spec(StepContext.getRequestSpecification())
    .body(body)
    .when()
    .post("/admin/rentals");

// ❌ 복잡한 로직을 Helper에 넣기
ApiHelper.createComplexBusinessLogicRequest(...); // 이런 메서드는 만들지 말 것

// ❌ 하드코딩된 URL 중복 사용
createExtractableResponse("GET", "http://localhost:8081/admin/rentals"); // baseUri는 StepContext에서 처리
```

### ✅ 권장 사항

```java
// ✅ static import로 깔끔하게
import static com.camping.admin.helper.ApiHelper.createExtractableResponseWithAuthorization;

// ✅ 의미 있는 메서드명으로 래핑
public static ExtractableResponse<Response> 관리자_권한으로_대여_목록_조회() {
    return createExtractableResponseWithAuthorization("GET", "/admin/rentals");
}

// ✅ 적절한 추상화 수준 유지
public static Map<String, Object> 특정_조건의_예약_찾기(Predicate<Map<String, Object>> condition) {
    return 예약_목록_조회().jsonPath()
            .<Map<String, Object>>getList("$").stream()
            .filter(condition)
            .findFirst()
            .orElse(null);
}
```

## 🎯 정리

이 헬퍼 시스템을 활용하면:

1. **코드 중복 최소화** - 같은 HTTP 요청 로직을 반복 작성할 필요 없음
2. **일관된 코드 스타일** - 모든 테스트에서 동일한 패턴 사용
3. **유지보수성 향상** - 공통 로직 변경 시 한 곳만 수정
4. **테스트 가독성 향상** - 비즈니스 로직에 집중 가능

헬퍼 시스템을 적극 활용하여 효율적인 테스트 코드를 작성하세요! 🚀