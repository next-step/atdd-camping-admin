# 🔄 레거시 코드에 ATDD 적용 가이드

기존에 만들어진 API나 코드에 ATDD 방식의 테스트를 적용하는 단계별 가이드입니다.

## 📋 개요

레거시 코드에 ATDD를 적용할 때는 **기존 동작을 보장**하면서 점진적으로 개선해나가는 것이 핵심입니다.

### 🎯 목적

- 기존 API 동작을 문서화하고 테스트 커버리지 확보
- 리팩토링 시 회귀 버그 방지
- 비즈니스 요구사항과 코드 동기화
- 점진적 코드 품질 개선

---

## 🔄 5단계 적용 프로세스

### 1️⃣ HTTP 파일 작성

**목적**: 기존 API의 실제 동작을 확인하고 문서화

```http
### 캠핑장 목록 조회 (기존 API)
GET http://localhost:8081/api/campsites
Authorization: Bearer {{accessToken}}

### 예상 응답 확인
# {
#   "campsites": [
#     {"id": 1, "name": "강원도 캠핑장", "capacity": 50}
#   ]
# }

### 캠핑장 예약 생성 (기존 API)
POST http://localhost:8081/api/reservations
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "campsiteId": 1,
  "guestName": "김철수",
  "checkInDate": "2024-07-01",
  "checkOutDate": "2024-07-03"
}
```

**체크포인트**:
- ✅ 모든 주요 엔드포인트가 실제로 동작하는가?
- ✅ 응답 형식이 예상과 일치하는가?
- ✅ 에러 케이스도 확인했는가?

### 2️⃣ API 스펙 명시

**목적**: 현재 API의 동작을 명확히 정의하고 문서화

`docs/api-reference.md`에 기존 API 스펙을 추가:

```markdown
## 예약 관리 API

### POST /api/reservations
**목적**: 새로운 예약을 생성합니다.

#### 요청 형식
```json
{
  "campsiteId": 1,           // 필수: 캠핑장 ID
  "guestName": "김철수",      // 필수: 예약자명 (2-50자)
  "checkInDate": "2024-07-01", // 필수: 체크인 날짜 (YYYY-MM-DD)
  "checkOutDate": "2024-07-03" // 필수: 체크아웃 날짜 (YYYY-MM-DD)
}
```

#### 응답 형식 (201 Created)
```json
{
  "reservationId": 1,
  "status": "CONFIRMED",
  "totalAmount": 150000
}
```

#### 에러 응답 (400 Bad Request)
```json
{
  "error": "VALIDATION_ERROR",
  "message": "체크인 날짜는 오늘 이후여야 합니다."
}
```
```

### 3️⃣ Gherkin 시나리오 작성

**목적**: 비즈니스 요구사항을 자연어로 표현하여 이해관계자와 소통

`src/test/resources/features/legacy-reservation.feature`:

```gherkin
Feature: 예약 관리 (레거시 API 테스트)
  캠핑장 예약을 생성하고 관리하는 기존 API의 동작을 검증한다.

  Background:
    Given 관리자로 로그인되어 있다
    And 다음 캠핑장이 등록되어 있다:
      | id | name        | capacity |
      | 1  | 강원도 캠핑장   | 50       |
      | 2  | 경기도 캠핑장   | 30       |

  Scenario: 정상적인 예약 생성
    When 관리자는 다음 예약을 생성한다:
      | campsiteId | guestName | checkInDate | checkOutDate |
      | 1         | 김철수     | 2024-07-01  | 2024-07-03   |
    Then 예약이 성공적으로 생성된다
    And 예약 상태는 "CONFIRMED"이다
    And 총 금액이 계산되어 반환된다

  Scenario: 잘못된 날짜로 예약 생성 시도
    When 관리자는 다음 예약을 생성한다:
      | campsiteId | guestName | checkInDate | checkOutDate |
      | 1         | 김철수     | 2024-06-30  | 2024-06-28   |
    Then 예약 생성이 실패한다
    And 에러 메시지가 "체크아웃 날짜는 체크인 날짜 이후여야 합니다"이다

  Scenario: 존재하지 않는 캠핑장으로 예약 시도
    When 관리자는 다음 예약을 생성한다:
      | campsiteId | guestName | checkInDate | checkOutDate |
      | 999       | 김철수     | 2024-07-01  | 2024-07-03   |
    Then 예약 생성이 실패한다
    And 에러 메시지가 "존재하지 않는 캠핑장입니다"이다
```

### 4️⃣ Cucumber 구현 (Steps, TestFixture, Hook)

**목적**: Gherkin 시나리오를 실제 실행 가능한 테스트로 구현

#### Steps 구현
`src/test/java/com/camping/admin/steps/LegacyReservationSteps.java`:

```java
@Component
public class LegacyReservationSteps {

    @Given("다음 캠핑장이 등록되어 있다:")
    public void 다음_캠핑장이_등록되어_있다(DataTable dataTable) {
        List<Map<String, String>> campsites = dataTable.asMaps();
        for (Map<String, String> campsite : campsites) {
            // 기존 데이터 설정은 data.sql에서 처리되므로 여기서는 검증만
            ReservationTestFixture.캠핑장_존재_확인(
                Long.parseLong(campsite.get("id"))
            );
        }
    }

    @When("관리자는 다음 예약을 생성한다:")
    public void 관리자는_다음_예약을_생성한다(DataTable dataTable) {
        Map<String, String> reservation = dataTable.asMaps().get(0);

        Map<String, Object> requestBody = Map.of(
            "campsiteId", Long.parseLong(reservation.get("campsiteId")),
            "guestName", reservation.get("guestName"),
            "checkInDate", reservation.get("checkInDate"),
            "checkOutDate", reservation.get("checkOutDate")
        );

        ExtractableResponse<Response> response = ReservationTestFixture.예약_생성_요청(requestBody);
        StepContext.setResponse(response);
    }

    @Then("예약이 성공적으로 생성된다")
    public void 예약이_성공적으로_생성된다() {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getLong("reservationId")).isNotNull();
    }

    @Then("예약 상태는 {string}이다")
    public void 예약_상태는_이다(String expectedStatus) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.jsonPath().getString("status")).isEqualTo(expectedStatus);
    }

    @Then("총 금액이 계산되어 반환된다")
    public void 총_금액이_계산되어_반환된다() {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.jsonPath().getInt("totalAmount")).isGreaterThan(0);
    }

    @Then("예약 생성이 실패한다")
    public void 예약_생성이_실패한다() {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Then("에러 메시지가 {string}이다")
    public void 에러_메시지가_이다(String expectedMessage) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.jsonPath().getString("message")).contains(expectedMessage);
    }
}
```

#### TestFixture 구현
`src/test/java/com/camping/admin/fixture/ReservationTestFixture.java`:

```java
public class ReservationTestFixture {

    public static ExtractableResponse<Response> 예약_생성_요청(Map<String, Object> requestBody) {
        return ApiHelper.createExtractableResponseWithAuthorization(
            "POST", "/api/reservations", requestBody
        );
    }

    public static void 캠핑장_존재_확인(Long campsiteId) {
        ExtractableResponse<Response> response = ApiHelper.createExtractableResponseWithAuthorization(
            "GET", "/api/campsites/" + campsiteId, null
        );

        if (response.statusCode() != 200) {
            throw new IllegalStateException("캠핑장 ID " + campsiteId + "가 존재하지 않습니다.");
        }
    }

    public static ExtractableResponse<Response> 예약_목록_조회() {
        return ApiHelper.createExtractableResponseWithAuthorization(
            "GET", "/api/reservations", null
        );
    }

    public static ExtractableResponse<Response> 특정_예약_조회(Long reservationId) {
        return ApiHelper.createExtractableResponseWithAuthorization(
            "GET", "/api/reservations/" + reservationId, null
        );
    }
}
```

### 5️⃣ 내부 코드 리팩토링 + TDD (단위 테스트)

**목적**: ATDD 테스트가 통과하는 상태에서 내부 구현을 개선

#### 리팩토링 전략

1. **현재 동작 보장**: ATDD 테스트가 모두 통과하는 상태 유지
2. **점진적 개선**: 한 번에 하나씩 작은 단위로 리팩토링
3. **단위 테스트 추가**: 복잡한 비즈니스 로직에 대한 단위 테스트 작성

#### 예제: 예약 서비스 리팩토링

**Before (레거시 코드)**:
```java
@RestController
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @PostMapping("/api/reservations")
    public ResponseEntity<?> createReservation(@RequestBody Map<String, Object> request) {
        // 모든 로직이 컨트롤러에 집중되어 있음
        Long campsiteId = (Long) request.get("campsiteId");
        String guestName = (String) request.get("guestName");
        // ... validation, business logic, persistence 모두 여기에
    }
}
```

**After (리팩토링 후)**:
```java
@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/api/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(201).body(response);
    }
}

@Service
public class ReservationService {

    public ReservationResponse createReservation(CreateReservationRequest request) {
        // 비즈니스 로직 분리
        validateReservationRequest(request);
        Reservation reservation = buildReservation(request);
        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    // 단위 테스트 가능한 메서드들로 분리
    private void validateReservationRequest(CreateReservationRequest request) { /* ... */ }
    private Reservation buildReservation(CreateReservationRequest request) { /* ... */ }
}
```

#### 단위 테스트 추가
`src/test/java/com/camping/admin/service/ReservationServiceTest.java`:

```java
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void 정상적인_예약_요청시_예약이_생성된다() {
        // given
        CreateReservationRequest request = CreateReservationRequest.builder()
            .campsiteId(1L)
            .guestName("김철수")
            .checkInDate(LocalDate.now().plusDays(1))
            .checkOutDate(LocalDate.now().plusDays(3))
            .build();

        Reservation savedReservation = createMockReservation();
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // when
        ReservationResponse response = reservationService.createReservation(request);

        // then
        assertThat(response.getReservationId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void 체크아웃날짜가_체크인날짜보다_이전이면_예외가_발생한다() {
        // given
        CreateReservationRequest request = CreateReservationRequest.builder()
            .campsiteId(1L)
            .guestName("김철수")
            .checkInDate(LocalDate.now().plusDays(3))
            .checkOutDate(LocalDate.now().plusDays(1))  // 잘못된 날짜
            .build();

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(request))
            .isInstanceOf(InvalidReservationException.class)
            .hasMessage("체크아웃 날짜는 체크인 날짜 이후여야 합니다");
    }
}
```

---

## 📊 진행 상황 체크리스트

### Phase 1: 현황 파악
- [ ] 레거시 API 엔드포인트 목록 작성
- [ ] HTTP 파일로 모든 API 동작 확인
- [ ] 현재 API 스펙 문서화
- [ ] 주요 비즈니스 룰 파악

### Phase 2: 테스트 커버리지
- [ ] Gherkin 시나리오 작성 (주요 기능)
- [ ] Cucumber Steps 구현
- [ ] TestFixture로 API 호출 추상화
- [ ] 모든 시나리오 테스트 통과 확인

### Phase 3: 리팩토링
- [ ] 컨트롤러에서 비즈니스 로직 분리
- [ ] DTO/Request/Response 객체 도입
- [ ] 서비스 레이어 분리
- [ ] 단위 테스트 추가

### Phase 4: 품질 개선
- [ ] 예외 처리 개선
- [ ] 로깅 추가
- [ ] 성능 최적화
- [ ] 보안 검토

---

## 🎯 모범 사례

### ✅ DO
- **점진적 접근**: 한 번에 하나의 API부터 시작
- **기존 동작 보장**: 리팩토링 전후 동일한 결과 보장
- **문서 우선**: 코드 변경 전 스펙 명확히 정의
- **테스트 자동화**: CI/CD에 ATDD 테스트 포함

### ❌ DON'T
- **한 번에 전체 변경**: 너무 큰 단위의 변경은 위험
- **문서 없는 변경**: API 스펙 변경 시 반드시 문서 업데이트
- **테스트 없는 리팩토링**: 회귀 테스트 없이 코드 변경 금지
- **기존 API 호환성 파괴**: 하위 호환성 유지 필수

---

## 🚀 실행 명령어

```bash
# 레거시 API ATDD 테스트 실행
./gradlew isolatedCucumberTest --tests="*Legacy*"

# 전체 테스트 실행
./gradlew test

# 리팩토링 후 회귀 테스트
./gradlew cucumberTest
```

---

**다음 단계**: [새로운 API ATDD 개발 가이드](./new-api-atdd-guide.md)로 진행하세요!