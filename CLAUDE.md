# 🏕️ 초록 캠핑장 관리자 시스템 - CLAUDE 가이드

## 📋 프로젝트 개요

### 기술 스택
- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 (인메모리)
- **Testing**: Cucumber + RestAssured (ATDD 방식)
- **Build Tool**: Gradle
- **Authentication**: JWT

### 시스템 아키텍처
```
atdd-camping-admin/
├── 📁 src/main/java/com/camping/admin/
│   ├── 🎯 controller/          # REST API 엔드포인트
│   ├── 🏗️ domain/
│   │   ├── entity/            # JPA 엔티티
│   │   └── enums/             # 열거형 정의
│   ├── 📦 dto/                 # 데이터 전송 객체
│   ├── 🔧 service/             # 비즈니스 로직
│   ├── 📊 repository/          # 데이터 접근 계층
│   └── ⚙️ config/             # 설정 파일
├── 📁 src/test/java/com/camping/admin/
│   ├── 🥒 CucumberTestRunner.java
│   ├── 🛠️ helper/              # 테스트 헬퍼 클래스
│   └── 📝 steps/               # Cucumber 스텝 정의
├── 📁 src/test/resources/
│   ├── 🥒 features/           # Gherkin 시나리오 파일
│   ├── 🗄️ cleanup.sql         # DB 정리 스크립트
│   └── 🗄️ restore-initial-data.sql
├── 📁 http/                    # HTTP 요청 테스트 파일
└── 📁 src/main/resources/
    ├── ⚙️ application.yml
    └── 🗄️ data.sql
```

## 🧪 테스트 개발 프로세스

### 1️⃣ HTTP 파일 생성
```http
### 예시: rental-admin.http
POST http://localhost:8081/admin/rentals
Authorization: Bearer {{authToken}}
Content-Type: application/json

{
  "reservationId": 1,
  "productId": 1,
  "quantity": 2
}
```

### 2️⃣ Gherkin 시나리오 작성
```gherkin
# src/test/resources/features/rental.feature
Feature: 대여 생성

  Scenario: 상품이 등록되어 있고 렌탈 상품이라면, 관리자는 특정 상품의 수량 만큼 대여 기록을 작성할 수 있다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 1 |
      | quantity      | 2 |
    Then 대여 기록이 생성된다.
    And 상품의 수량이 대여 기록의 수량 만큼 줄어든다.
```

### 3️⃣ Cucumber Steps 구현
```java
import java.util.Map;// src/test/java/com/camping/admin/steps/RentalSteps.java
@When("관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.")
public void 관리자는_특정_상품의_수량_만큼_대여_기록을_작성한다(DataTable dataTable) {
    Map<String, Integer> params = dataTable.asMap(String.class, Integer.class);
    
    response = RentalTestFixture.대여_기록_작성_요청(params);
    StepContext.setResponse(response);
}
```

### 4️⃣ Test Fixture 활용
```java
// src/test/java/com/camping/admin/steps/RentalTestFixture.java
public static ExtractableResponse<Response> 대여_기록_작성_요청(Map<String, Integer> body) {
    return ApiHelper.createExtractableResponseWithAuthorization("POST", "/admin/rentals", body);
}

public static ExtractableResponse<Response> 대여_기록_목록_조회() {
    ExtractableResponse<Response> response = ApiHelper.createExtractableResponseWithAuthorization("GET", "/admin/rentals");
    assertThat(response.statusCode()).isEqualTo(200);
    return response;
}
```

## 🛠️ 테스트 헬퍼 시스템

### ApiHelper - 전략 패턴 적용
```java
// 기본 사용법
ApiHelper.createExtractableResponse("GET", "/admin/rentals");
ApiHelper.createExtractableResponseWithAuthorization("POST", "/admin/rentals", body);

// 내부 구조
public class ApiHelper {
    private static final RestAssuredHelperImpl restAssuredHelper = new RestAssuredHelperImpl();
    
    public static <T> ExtractableResponse createExtractableResponseWithAuthorization(String httpMethod, String url, T body) {
        return restAssuredHelper.execute(HttpMethod.fromString(httpMethod), url, body, true);
    }
}
```

### HTTP Method 전략 패턴
```java
public interface HttpMethodStrategy {
    <T> ExtractableResponse execute(RequestSpecification requestSpec, String url, T body);
    boolean supports(HttpMethod method);
}

// 구현체: GetStrategy, PostStrategy, PutStrategy, PatchStrategy, DeleteStrategy
```

### RestAssuredHelper
```java
public class RestAssuredHelper {
    private static final List<HttpMethodStrategy> strategies = List.of(
        new GetStrategy(), new PostStrategy(), new PutStrategy(), 
        new PatchStrategy(), new DeleteStrategy()
    );
    
    public <T> ExtractableResponse execute(HttpMethod method, String url, T body, boolean needAuthorization) {
        // 전략 패턴으로 적절한 HTTP 메서드 실행
        for (HttpMethodStrategy strategy : strategies) {
            if (strategy.supports(method)) {
                return strategy.execute(requestSpec, url, body);
            }
        }
    }
}
```

## 🗄️ 데이터베이스 관리

### 초기 데이터 (data.sql)
- **Campsites**: 캠프사이트 정보 (A-01, A-02)
- **Products**: 대여/판매 상품 (랜턴, 장작팩 등)
- **Reservations**: 예약 정보 (최근 한달 데이터 포함)
- **Sales Records**: 판매 기록


## 🎯 테스트 실행 흐름

### Hooks를 통한 자동 관리
```java
public class Hooks {
    @BeforeAll
    public static void initAccessToken() {
        // JWT 토큰 획득
    }
    
    @Before
    public void beforeScenario(Scenario scenario) {
        StepContext.setSpec();
    }
    
    @After
    public void afterScenario(Scenario scenario) {
    }
}
```

### StepContext - 테스트 상태 관리
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
}
```

## 📊 주요 도메인

### 엔티티 구조
- **Campsite**: 캠프사이트 관리
- **Product**: 상품 관리 (RENTAL/SALE 타입)
- **Reservation**: 예약 관리
- **RentalRecord**: 대여 기록
- **SalesRecord**: 판매 기록

### API 엔드포인트
```
POST /auth/login                    # 인증
GET  /admin/reservations           # 예약 목록
PATCH /admin/reservations/{id}/status # 예약 상태 변경
GET  /admin/rentals                # 대여 목록  
POST /admin/rentals                # 대여 기록 생성
GET  /admin/products               # 상품 목록
POST /admin/sales                  # 판매 처리
GET  /admin/revenue/daily          # 일일 매출
```

## 🚀 개발 가이드

### 새로운 기능 추가 시
1. **HTTP 파일 작성** → API 동작 확인
2. **Gherkin 시나리오 작성** → 비즈니스 요구사항 정의
3. **Steps 구현** → 테스트 로직 작성
4. **TestFixture 활용** → API 호출 추상화
5. **ApiHelper 사용** → HTTP 요청 단순화

### 테스트 작성 원칙
- ✅ **SpringBootTest 사용 금지** - 순수 RestAssured 사용
- ✅ **Helper 적극 활용** - 코드 중복 최소화
- ✅ **데이터 격리** - 각 시나리오별 독립적 실행
- ✅ **의미 있는 메서드명** - 한글 메서드명으로 가독성 향상

### 권장 네이밍 컨벤션
```java
// TestFixture 메서드
public static ExtractableResponse<Response> 대여_기록_목록_조회()
public static Map<String, Object> 특정_대여_기록_조회(long rentalId)
public static ExtractableResponse<Response> 대여_기록_작성_요청(Map<String, Integer> body)

// Steps 메서드  
@When("관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.")
@Then("대여 기록이 생성된다.")
@And("상품의 수량이 대여 기록의 수량 만큼 줄어든다.")
```

## 🔧 설정 정보

### 애플리케이션 설정
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    defer-datasource-initialization: true
  sql:
    init:
      mode: always

server:
  port: 8081

jwt:
  secret: "change-this-dev-secret-key-please"
  expiration-minutes: 30

admin:
  username: admin
  password: admin123
```

### 접속 정보
- **애플리케이션**: http://localhost:8081
- **H2 콘솔**: http://localhost:8081/h2-console
- **관리자 계정**: admin / admin123

이 가이드를 통해 일관된 ATDD 방식의 테스트 개발이 가능합니다. 🎯