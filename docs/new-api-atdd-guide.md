# 🆕 새로운 API ATDD 개발 가이드

처음부터 ATDD 방식으로 새로운 API를 개발하는 단계별 가이드입니다.

## 📋 개요

새로운 기능을 개발할 때는 **테스트부터 작성**하여 요구사항을 명확히 하고, **외부에서 내부로** 점진적으로 구현해나가는 것이 핵심입니다.

### 🎯 목적

- 요구사항을 명확하게 정의하고 이해관계자와 소통
- 테스트 가능한 설계로 품질 높은 코드 작성
- 비즈니스 가치 중심의 개발 진행
- 회귀 테스트 자동화로 안정성 확보

---

## 🚀 7단계 개발 프로세스

### 1️⃣ PRD (Product Requirements Document) 작성

**목적**: 비즈니스 요구사항과 기술 스펙을 명확히 정의

`docs/prd/campsite-availability-check.md`:

```markdown
# 캠핑장 실시간 예약 가능성 조회 API

## 📋 비즈니스 요구사항

### 배경
- 고객이 특정 날짜에 예약 가능한 캠핑장을 빠르게 찾고 싶어함
- 현재는 각 캠핑장을 개별적으로 확인해야 하는 번거로움 존재

### 목표
- 원하는 기간에 예약 가능한 캠핑장 목록을 한 번에 조회
- 실시간 예약 현황 반영으로 정확한 정보 제공
- 응답 시간 1초 이내로 빠른 검색 제공

## 🎯 기능 명세

### 주요 기능
1. **기간별 예약 가능 캠핑장 조회**
   - 체크인/체크아웃 날짜로 검색
   - 예약 가능한 캠핑장만 필터링
   - 남은 자리 수 표시

2. **추가 필터링 옵션**
   - 지역별 필터링
   - 최대 수용인원 필터링
   - 가격 범위 필터링

### 비즈니스 룰
- 체크인 날짜는 오늘 이후여야 함
- 체크아웃 날짜는 체크인 날짜 이후여야 함
- 최대 검색 기간은 30일
- 예약 불가능한 캠핑장은 결과에서 제외

## 🔧 기술 스펙

### API 엔드포인트
- `GET /api/campsites/availability`

### 요청 파라미터
- `checkInDate`: 체크인 날짜 (필수, YYYY-MM-DD)
- `checkOutDate`: 체크아웃 날짜 (필수, YYYY-MM-DD)
- `region`: 지역 필터 (선택사항)
- `minCapacity`: 최소 수용인원 (선택사항)
- `maxPrice`: 최대 가격 (선택사항)

### 응답 형식
```json
{
  "availableCampsites": [
    {
      "id": 1,
      "name": "강원도 캠핑장",
      "region": "강원도",
      "capacity": 50,
      "availableSpots": 15,
      "pricePerNight": 50000,
      "amenities": ["화장실", "샤워실", "Wi-Fi"]
    }
  ],
  "totalCount": 5,
  "searchCriteria": {
    "checkInDate": "2024-07-01",
    "checkOutDate": "2024-07-03",
    "region": "강원도"
  }
}
```

## 🧪 테스트 시나리오
1. 정상적인 기간 검색 시 예약 가능한 캠핑장 목록 반환
2. 예약이 가득 찬 기간 검색 시 빈 목록 반환
3. 잘못된 날짜 형식 입력 시 400 에러
4. 과거 날짜 검색 시 400 에러
5. 30일을 초과하는 기간 검색 시 400 에러

## 📊 성공 지표
- API 응답 시간 < 1초
- 검색 정확도 99% 이상
- 동시 사용자 100명 처리 가능
```

### 2️⃣ Gherkin 시나리오 작성

**목적**: PRD를 바탕으로 구체적인 테스트 시나리오 정의

`src/test/resources/features/campsite-availability.feature`:

```gherkin
Feature: 캠핑장 예약 가능성 조회
  고객이 원하는 기간에 예약 가능한 캠핑장을 조회할 수 있다.

  Background:
    Given 관리자로 로그인되어 있다
    And 다음 캠핑장들이 등록되어 있다:
      | id | name        | region | capacity | pricePerNight |
      | 1  | 강원도 캠핑장  | 강원도  | 50      | 50000        |
      | 2  | 경기도 캠핑장  | 경기도  | 30      | 40000        |
      | 3  | 부산 해변 캠핑장 | 부산   | 20      | 60000        |
    And 다음 예약들이 존재한다:
      | campsiteId | checkInDate | checkOutDate | guestCount |
      | 1         | 2024-07-01  | 2024-07-03   | 30        |
      | 2         | 2024-07-01  | 2024-07-02   | 25        |

  Scenario: 예약 가능한 캠핑장 조회
    When 클라이언트가 다음 조건으로 예약 가능한 캠핑장을 조회한다:
      | checkInDate | checkOutDate |
      | 2024-07-01  | 2024-07-03   |
    Then 예약 가능한 캠핑장 목록이 반환된다
    And 응답에 다음 캠핑장들이 포함되어 있다:
      | name        | availableSpots |
      | 강원도 캠핑장  | 20            |
      | 부산 해변 캠핑장 | 20            |
    And 응답에 "경기도 캠핑장"은 포함되어 있지 않다

  Scenario: 지역 필터로 캠핑장 조회
    When 클라이언트가 다음 조건으로 예약 가능한 캠핑장을 조회한다:
      | checkInDate | checkOutDate | region |
      | 2024-07-05  | 2024-07-07   | 강원도  |
    Then 예약 가능한 캠핑장 목록이 반환된다
    And 응답에 "강원도 캠핑장"만 포함되어 있다
    And 총 결과 수는 1이다

  Scenario: 잘못된 날짜 형식으로 조회 시도
    When 클라이언트가 다음 조건으로 예약 가능한 캠핑장을 조회한다:
      | checkInDate | checkOutDate |
      | 2024/07/01  | 2024-07-03   |
    Then 요청이 실패한다
    And 에러 코드가 400이다
    And 에러 메시지가 "날짜 형식이 올바르지 않습니다 (YYYY-MM-DD)"이다

  Scenario: 과거 날짜로 조회 시도
    When 클라이언트가 다음 조건으로 예약 가능한 캠핑장을 조회한다:
      | checkInDate | checkOutDate |
      | 2024-01-01  | 2024-01-03   |
    Then 요청이 실패한다
    And 에러 코드가 400이다
    And 에러 메시지가 "체크인 날짜는 오늘 이후여야 합니다"이다

  Scenario: 체크아웃이 체크인보다 이전인 경우
    When 클라이언트가 다음 조건으로 예약 가능한 캠핑장을 조회한다:
      | checkInDate | checkOutDate |
      | 2024-07-05  | 2024-07-03   |
    Then 요청이 실패한다
    And 에러 코드가 400이다
    And 에러 메시지가 "체크아웃 날짜는 체크인 날짜 이후여야 합니다"이다

  Scenario: 30일을 초과하는 기간으로 조회
    When 클라이언트가 다음 조건으로 예약 가능한 캠핑장을 조회한다:
      | checkInDate | checkOutDate |
      | 2024-07-01  | 2024-08-15   |
    Then 요청이 실패한다
    And 에러 코드가 400이다
    And 에러 메시지가 "최대 검색 기간은 30일입니다"이다
```

### 3️⃣ Controller Mock API 작성

**목적**: Gherkin 시나리오가 실행될 수 있도록 최소한의 목 구현

`src/main/java/com/camping/admin/controller/CampsiteAvailabilityController.java`:

```java
@RestController
@RequestMapping("/api/campsites")
public class CampsiteAvailabilityController {

    @GetMapping("/availability")
    public ResponseEntity<?> checkAvailability(
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxPrice) {

        // TODO: 실제 구현 전 목 응답
        Map<String, Object> mockResponse = Map.of(
            "availableCampsites", List.of(),
            "totalCount", 0,
            "searchCriteria", Map.of(
                "checkInDate", checkInDate,
                "checkOutDate", checkOutDate
            )
        );

        return ResponseEntity.ok(mockResponse);
    }
}
```

### 4️⃣ HTTP 파일 작성

**목적**: API 동작을 수동으로 확인하고 빠른 피드백 받기

`http/campsite-availability.http`:

```http
### 캠핑장 예약 가능성 조회 - 기본 검색
GET http://localhost:8081/api/campsites/availability?checkInDate=2024-07-01&checkOutDate=2024-07-03
Authorization: Bearer {{accessToken}}

### 캠핑장 예약 가능성 조회 - 지역 필터
GET http://localhost:8081/api/campsites/availability?checkInDate=2024-07-01&checkOutDate=2024-07-03&region=강원도
Authorization: Bearer {{accessToken}}

### 캠핑장 예약 가능성 조회 - 수용인원 필터
GET http://localhost:8081/api/campsites/availability?checkInDate=2024-07-01&checkOutDate=2024-07-03&minCapacity=40
Authorization: Bearer {{accessToken}}

### 캠핑장 예약 가능성 조회 - 가격 필터
GET http://localhost:8081/api/campsites/availability?checkInDate=2024-07-01&checkOutDate=2024-07-03&maxPrice=55000
Authorization: Bearer {{accessToken}}

### 에러 케이스 - 잘못된 날짜 형식
GET http://localhost:8081/api/campsites/availability?checkInDate=2024/07/01&checkOutDate=2024-07-03
Authorization: Bearer {{accessToken}}

### 에러 케이스 - 과거 날짜
GET http://localhost:8081/api/campsites/availability?checkInDate=2024-01-01&checkOutDate=2024-01-03
Authorization: Bearer {{accessToken}}

### 에러 케이스 - 체크아웃이 체크인보다 이전
GET http://localhost:8081/api/campsites/availability?checkInDate=2024-07-05&checkOutDate=2024-07-03
Authorization: Bearer {{accessToken}}

### 에러 케이스 - 30일 초과 기간
GET http://localhost:8081/api/campsites/availability?checkInDate=2024-07-01&checkOutDate=2024-08-15
Authorization: Bearer {{accessToken}}
```

### 5️⃣ Cucumber 구현 (Steps, Hooks, TestFixture)

**목적**: Gherkin 시나리오를 실행 가능한 테스트로 구현

#### Steps 구현
`src/test/java/com/camping/admin/steps/CampsiteAvailabilitySteps.java`:

```java
@Component
public class CampsiteAvailabilitySteps {

    @Given("다음 캠핑장들이 등록되어 있다:")
    public void 다음_캠핑장들이_등록되어_있다(DataTable dataTable) {
        // data.sql에서 기본 데이터가 로드되므로 여기서는 검증만 수행
        List<Map<String, String>> campsites = dataTable.asMaps();
        for (Map<String, String> campsite : campsites) {
            CampsiteAvailabilityTestFixture.캠핑장_존재_확인(
                Long.parseLong(campsite.get("id"))
            );
        }
    }

    @Given("다음 예약들이 존재한다:")
    public void 다음_예약들이_존재한다(DataTable dataTable) {
        List<Map<String, String>> reservations = dataTable.asMaps();
        for (Map<String, String> reservation : reservations) {
            CampsiteAvailabilityTestFixture.예약_생성(
                Long.parseLong(reservation.get("campsiteId")),
                reservation.get("checkInDate"),
                reservation.get("checkOutDate"),
                Integer.parseInt(reservation.get("guestCount"))
            );
        }
    }

    @When("클라이언트가 다음 조건으로 예약 가능한 캠핑장을 조회한다:")
    public void 클라이언트가_다음_조건으로_예약_가능한_캠핑장을_조회한다(DataTable dataTable) {
        Map<String, String> params = dataTable.asMaps().get(0);

        ExtractableResponse<Response> response = CampsiteAvailabilityTestFixture.예약_가능성_조회(
            params.get("checkInDate"),
            params.get("checkOutDate"),
            params.get("region"),
            params.get("minCapacity"),
            params.get("maxPrice")
        );

        StepContext.setResponse(response);
    }

    @Then("예약 가능한 캠핑장 목록이 반환된다")
    public void 예약_가능한_캠핑장_목록이_반환된다() {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("availableCampsites")).isNotNull();
    }

    @Then("응답에 다음 캠핑장들이 포함되어 있다:")
    public void 응답에_다음_캠핑장들이_포함되어_있다(DataTable dataTable) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        List<Map<String, Object>> availableCampsites = response.jsonPath().getList("availableCampsites");

        List<Map<String, String>> expectedCampsites = dataTable.asMaps();
        for (Map<String, String> expected : expectedCampsites) {
            boolean found = availableCampsites.stream()
                .anyMatch(campsite -> {
                    String name = (String) campsite.get("name");
                    Integer availableSpots = (Integer) campsite.get("availableSpots");
                    return expected.get("name").equals(name) &&
                           Integer.parseInt(expected.get("availableSpots")) == availableSpots;
                });

            assertThat(found)
                .as("캠핑장 '%s'가 예상 가능 자리 수 '%s'와 함께 결과에 포함되어야 함",
                    expected.get("name"), expected.get("availableSpots"))
                .isTrue();
        }
    }

    @Then("응답에 {string}은 포함되어 있지 않다")
    public void 응답에_은_포함되어_있지_않다(String campsiteName) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        List<Map<String, Object>> availableCampsites = response.jsonPath().getList("availableCampsites");

        boolean found = availableCampsites.stream()
            .anyMatch(campsite -> campsiteName.equals(campsite.get("name")));

        assertThat(found)
            .as("캠핑장 '%s'가 결과에 포함되어서는 안 됨", campsiteName)
            .isFalse();
    }

    @Then("응답에 {string}만 포함되어 있다")
    public void 응답에_만_포함되어_있다(String expectedCampsiteName) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        List<Map<String, Object>> availableCampsites = response.jsonPath().getList("availableCampsites");

        assertThat(availableCampsites).hasSize(1);
        assertThat(availableCampsites.get(0).get("name")).isEqualTo(expectedCampsiteName);
    }

    @Then("총 결과 수는 {int}이다")
    public void 총_결과_수는_이다(int expectedCount) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.jsonPath().getInt("totalCount")).isEqualTo(expectedCount);
    }

    @Then("요청이 실패한다")
    public void 요청이_실패한다() {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.statusCode()).isBetween(400, 499);
    }

    @Then("에러 코드가 {int}이다")
    public void 에러_코드가_이다(int expectedErrorCode) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(expectedErrorCode);
    }

    @Then("에러 메시지가 {string}이다")
    public void 에러_메시지가_이다(String expectedMessage) {
        ExtractableResponse<Response> response = StepContext.getResponse();
        String actualMessage = response.jsonPath().getString("message");
        assertThat(actualMessage).contains(expectedMessage);
    }
}
```

#### TestFixture 구현
`src/test/java/com/camping/admin/fixture/CampsiteAvailabilityTestFixture.java`:

```java
public class CampsiteAvailabilityTestFixture {

    public static ExtractableResponse<Response> 예약_가능성_조회(
            String checkInDate, String checkOutDate, String region,
            String minCapacity, String maxPrice) {

        StringBuilder url = new StringBuilder("/api/campsites/availability")
            .append("?checkInDate=").append(checkInDate)
            .append("&checkOutDate=").append(checkOutDate);

        if (region != null) {
            url.append("&region=").append(region);
        }
        if (minCapacity != null) {
            url.append("&minCapacity=").append(minCapacity);
        }
        if (maxPrice != null) {
            url.append("&maxPrice=").append(maxPrice);
        }

        return ApiHelper.createExtractableResponseWithAuthorization(
            "GET", url.toString(), null
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

    public static void 예약_생성(Long campsiteId, String checkInDate, String checkOutDate, int guestCount) {
        Map<String, Object> reservationData = Map.of(
            "campsiteId", campsiteId,
            "guestName", "테스트 예약자",
            "checkInDate", checkInDate,
            "checkOutDate", checkOutDate,
            "guestCount", guestCount
        );

        ExtractableResponse<Response> response = ApiHelper.createExtractableResponseWithAuthorization(
            "POST", "/api/reservations", reservationData
        );

        if (response.statusCode() != 201) {
            throw new IllegalStateException("테스트용 예약 생성에 실패했습니다: " + response.body().asString());
        }
    }
}
```

### 6️⃣ 내부 코드 작성 (TDD)

**목적**: 테스트가 통과하도록 내부 비즈니스 로직 구현

#### Service 레이어 구현
`src/main/java/com/camping/admin/service/CampsiteAvailabilityService.java`:

```java
@Service
@Transactional(readOnly = true)
public class CampsiteAvailabilityService {

    private final CampsiteRepository campsiteRepository;
    private final ReservationRepository reservationRepository;

    public CampsiteAvailabilityService(CampsiteRepository campsiteRepository,
                                      ReservationRepository reservationRepository) {
        this.campsiteRepository = campsiteRepository;
        this.reservationRepository = reservationRepository;
    }

    public CampsiteAvailabilityResponse findAvailableCampsites(CampsiteAvailabilityRequest request) {
        // 1. 입력 검증
        validateSearchRequest(request);

        // 2. 모든 캠핑장 조회 (필터 적용)
        List<Campsite> allCampsites = findCampsitesWithFilters(request);

        // 3. 각 캠핑장별 예약 가능성 계산
        List<AvailableCampsiteDto> availableCampsites = allCampsites.stream()
            .map(campsite -> calculateAvailability(campsite, request))
            .filter(dto -> dto.getAvailableSpots() > 0)
            .collect(Collectors.toList());

        // 4. 응답 생성
        return CampsiteAvailabilityResponse.builder()
            .availableCampsites(availableCampsites)
            .totalCount(availableCampsites.size())
            .searchCriteria(SearchCriteriaDto.from(request))
            .build();
    }

    private void validateSearchRequest(CampsiteAvailabilityRequest request) {
        LocalDate now = LocalDate.now();
        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();

        if (checkIn.isBefore(now)) {
            throw new InvalidSearchException("체크인 날짜는 오늘 이후여야 합니다");
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new InvalidSearchException("체크아웃 날짜는 체크인 날짜 이후여야 합니다");
        }

        if (ChronoUnit.DAYS.between(checkIn, checkOut) > 30) {
            throw new InvalidSearchException("최대 검색 기간은 30일입니다");
        }
    }

    private List<Campsite> findCampsitesWithFilters(CampsiteAvailabilityRequest request) {
        if (request.hasFilters()) {
            return campsiteRepository.findWithFilters(
                request.getRegion(),
                request.getMinCapacity(),
                request.getMaxPrice()
            );
        }
        return campsiteRepository.findAll();
    }

    private AvailableCampsiteDto calculateAvailability(Campsite campsite, CampsiteAvailabilityRequest request) {
        // 해당 기간에 겹치는 예약들의 총 인원 수 계산
        int reservedSpots = reservationRepository.countReservedSpots(
            campsite.getId(),
            request.getCheckInDate(),
            request.getCheckOutDate()
        );

        int availableSpots = Math.max(0, campsite.getCapacity() - reservedSpots);

        return AvailableCampsiteDto.builder()
            .id(campsite.getId())
            .name(campsite.getName())
            .region(campsite.getRegion())
            .capacity(campsite.getCapacity())
            .availableSpots(availableSpots)
            .pricePerNight(campsite.getPricePerNight())
            .amenities(campsite.getAmenities())
            .build();
    }
}
```

#### Controller 실제 구현으로 교체
`src/main/java/com/camping/admin/controller/CampsiteAvailabilityController.java`:

```java
@RestController
@RequestMapping("/api/campsites")
public class CampsiteAvailabilityController {

    private final CampsiteAvailabilityService availabilityService;

    public CampsiteAvailabilityController(CampsiteAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/availability")
    public ResponseEntity<CampsiteAvailabilityResponse> checkAvailability(
            @Valid CampsiteAvailabilityRequest request) {

        CampsiteAvailabilityResponse response = availabilityService.findAvailableCampsites(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(InvalidSearchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSearchException(InvalidSearchException e) {
        ErrorResponse error = ErrorResponse.builder()
            .error("INVALID_SEARCH_CRITERIA")
            .message(e.getMessage())
            .timestamp(Instant.now())
            .build();

        return ResponseEntity.badRequest().body(error);
    }
}
```

#### 단위 테스트 작성
`src/test/java/com/camping/admin/service/CampsiteAvailabilityServiceTest.java`:

```java
@ExtendWith(MockitoExtension.class)
class CampsiteAvailabilityServiceTest {

    @Mock
    private CampsiteRepository campsiteRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CampsiteAvailabilityService availabilityService;

    @Test
    void 정상적인_검색_요청시_예약_가능한_캠핑장_목록을_반환한다() {
        // given
        CampsiteAvailabilityRequest request = createValidRequest();
        List<Campsite> mockCampsites = createMockCampsites();

        when(campsiteRepository.findAll()).thenReturn(mockCampsites);
        when(reservationRepository.countReservedSpots(1L, request.getCheckInDate(), request.getCheckOutDate()))
            .thenReturn(30);
        when(reservationRepository.countReservedSpots(2L, request.getCheckInDate(), request.getCheckOutDate()))
            .thenReturn(25);

        // when
        CampsiteAvailabilityResponse response = availabilityService.findAvailableCampsites(request);

        // then
        assertThat(response.getAvailableCampsites()).hasSize(2);
        assertThat(response.getTotalCount()).isEqualTo(2);

        AvailableCampsiteDto firstCampsite = response.getAvailableCampsites().get(0);
        assertThat(firstCampsite.getAvailableSpots()).isEqualTo(20); // 50 - 30
    }

    @Test
    void 과거_날짜로_검색시_예외가_발생한다() {
        // given
        CampsiteAvailabilityRequest request = CampsiteAvailabilityRequest.builder()
            .checkInDate(LocalDate.now().minusDays(1))
            .checkOutDate(LocalDate.now().plusDays(1))
            .build();

        // when & then
        assertThatThrownBy(() -> availabilityService.findAvailableCampsites(request))
            .isInstanceOf(InvalidSearchException.class)
            .hasMessage("체크인 날짜는 오늘 이후여야 합니다");
    }

    @Test
    void 체크아웃이_체크인보다_이전인_경우_예외가_발생한다() {
        // given
        CampsiteAvailabilityRequest request = CampsiteAvailabilityRequest.builder()
            .checkInDate(LocalDate.now().plusDays(5))
            .checkOutDate(LocalDate.now().plusDays(3))
            .build();

        // when & then
        assertThatThrownBy(() -> availabilityService.findAvailableCampsites(request))
            .isInstanceOf(InvalidSearchException.class)
            .hasMessage("체크아웃 날짜는 체크인 날짜 이후여야 합니다");
    }

    private CampsiteAvailabilityRequest createValidRequest() {
        return CampsiteAvailabilityRequest.builder()
            .checkInDate(LocalDate.now().plusDays(1))
            .checkOutDate(LocalDate.now().plusDays(3))
            .build();
    }

    private List<Campsite> createMockCampsites() {
        return List.of(
            Campsite.builder().id(1L).name("강원도 캠핑장").capacity(50).build(),
            Campsite.builder().id(2L).name("경기도 캠핑장").capacity(30).build()
        );
    }
}
```

### 7️⃣ API 스펙 최신화

**목적**: 실제 구현이 완료된 후 API 문서를 최종 업데이트

`docs/api-reference.md`에 실제 API 스펙 추가:

```markdown
## 캠핑장 예약 가능성 조회 API

### GET /api/campsites/availability
**목적**: 지정된 기간에 예약 가능한 캠핑장 목록을 조회합니다.

#### 쿼리 파라미터
| 파라미터     | 타입   | 필수 | 설명                    |
|-------------|-------|-----|------------------------|
| checkInDate | String| Y   | 체크인 날짜 (YYYY-MM-DD) |
| checkOutDate| String| Y   | 체크아웃 날짜 (YYYY-MM-DD)|
| region      | String| N   | 지역 필터               |
| minCapacity | Integer|N   | 최소 수용인원           |
| maxPrice    | Integer|N   | 최대 가격 (원)         |

#### 성공 응답 (200 OK)
```json
{
  "availableCampsites": [
    {
      "id": 1,
      "name": "강원도 캠핑장",
      "region": "강원도",
      "capacity": 50,
      "availableSpots": 20,
      "pricePerNight": 50000,
      "amenities": ["화장실", "샤워실", "Wi-Fi"]
    }
  ],
  "totalCount": 1,
  "searchCriteria": {
    "checkInDate": "2024-07-01",
    "checkOutDate": "2024-07-03",
    "region": "강원도"
  }
}
```

#### 에러 응답 (400 Bad Request)
```json
{
  "error": "INVALID_SEARCH_CRITERIA",
  "message": "체크인 날짜는 오늘 이후여야 합니다",
  "timestamp": "2024-06-20T10:30:00Z"
}
```

#### 비즈니스 룰
- 체크인 날짜는 오늘 이후여야 함
- 체크아웃 날짜는 체크인 날짜 이후여야 함
- 최대 검색 기간은 30일
- 예약이 꽉 찬 캠핑장은 결과에서 제외됨
```

---

## 📊 진행 상황 체크리스트

### Phase 1: 기획 및 설계
- [ ] PRD 작성 및 이해관계자 검토
- [ ] Gherkin 시나리오 작성
- [ ] API 인터페이스 설계
- [ ] 목 컨트롤러 구현

### Phase 2: 테스트 구현
- [ ] HTTP 파일로 API 동작 확인
- [ ] Cucumber Steps 구현
- [ ] TestFixture 작성
- [ ] 모든 시나리오 실행 (목 응답으로 실패 예상)

### Phase 3: 실제 구현
- [ ] DTO/Request/Response 클래스 작성
- [ ] Service 레이어 구현
- [ ] Repository 메서드 구현
- [ ] Controller 실제 구현으로 교체

### Phase 4: 테스트 완성
- [ ] 모든 Cucumber 시나리오 통과
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 실행
- [ ] API 스펙 문서 최신화

---

## 🎯 모범 사례

### ✅ DO
- **요구사항 먼저**: 구현 전에 PRD와 시나리오를 명확히 정의
- **외부에서 내부로**: API → Service → Repository 순으로 개발
- **빠른 피드백**: HTTP 파일과 목 구현으로 빠른 검증
- **테스트 주도**: 테스트가 실패하는 것을 확인 후 구현

### ❌ DON'T
- **구현부터 시작**: 요구사항 정의 없이 코드 작성 금지
- **완벽한 설계 추구**: 초기에 모든 것을 완벽하게 설계하려 하지 말 것
- **테스트 생략**: 귀찮다고 테스트 작성을 건너뛰지 말 것
- **문서 미동기화**: 구현 후 API 스펙 업데이트 잊지 말 것

---

## 🚀 실행 명령어

```bash
# 새 API ATDD 테스트 실행
./gradlew isolatedCucumberTest --tests="*CampsiteAvailability*"

# 단위 테스트 실행
./gradlew test --tests="*CampsiteAvailabilityServiceTest"

# 전체 테스트 실행
./gradlew test

# HTTP 파일로 수동 테스트
# IDE의 HTTP Client 사용하여 http/campsite-availability.http 실행
```

---

## 📈 다음 단계

1. **성능 최적화**: 쿼리 최적화 및 캐싱 적용
2. **모니터링 추가**: 로그 및 메트릭 수집
3. **보안 강화**: Rate limiting 및 입력 검증 강화
4. **문서화 완성**: API 문서 자동 생성 설정

**관련 문서**: [레거시 코드 ATDD 적용 가이드](./legacy-atdd-guide.md)