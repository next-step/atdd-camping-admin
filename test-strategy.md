# 테스트 전략 문서

## 테스트 도구 구성

### 1. ScenarioContext

**위치**: `src/test/java/com/camping/admin/support/ScenarioContext.java`

**목적**: 시나리오 내 여러 Step 클래스 간 상태 공유

**특징**:
- `@ScenarioScope` 적용으로 시나리오마다 새 인스턴스 생성
- 테스트 격리 보장
- Given → When → Then 간 데이터 전달

**사용 예시**:
```java
@Autowired
private ScenarioContext scenarioContext;

@Given("확정된 예약이 있다")
public void 확정된_예약이_있다() {
    Reservation reservation = testDataFactory.createConfirmedReservation();
    scenarioContext.setReservation(reservation);  // 상태 저장
}

@When("관리자가 해당 예약을 취소한다")
public void 관리자가_해당_예약을_취소한다() {
    Reservation reservation = scenarioContext.getReservation();  // 상태 조회
    // ...
}
```

**관리 대상**:

| 필드 | 타입 | 설명 |
|------|------|------|
| reservation | Reservation | 현재 시나리오의 예약 엔티티 |
| response | Response | API 응답 객체 |

---

### 2. TestDataFactory

**위치**: `src/test/java/com/camping/admin/support/TestDataFactory.java`

**목적**: 테스트용 엔티티 생성 팩토리

**특징**:
- 하드코딩된 ID 의존성 제거
- 테스트마다 새 데이터 생성으로 격리 보장
- 필요한 연관 엔티티 자동 생성

**제공 메서드**:

| 메서드 | 설명 |
|--------|------|
| `createConfirmedReservation()` | CONFIRMED 상태의 예약 생성 |
| `createReservationWithStatus(String status)` | 지정된 상태의 예약 생성 |

**사용 예시**:
```java
@Autowired
private TestDataFactory testDataFactory;

@Given("확정된 예약이 있다")
public void 확정된_예약이_있다() {
    Reservation reservation = testDataFactory.createConfirmedReservation();
    // ...
}

@Given("{string} 상태의 예약이 있다")
public void 특정_상태의_예약이_있다(String status) {
    Reservation reservation = testDataFactory.createReservationWithStatus(status);
    // ...
}
```

---

## 테스트 구조

```
src/test/java/com/camping/admin/
├── support/
│   ├── CucumberTestRunner.java    # Cucumber 실행기
│   ├── ScenarioContext.java       # 시나리오 상태 관리
│   └── TestDataFactory.java       # 테스트 데이터 생성
└── steps/
    ├── CucumberSpringConfiguration.java  # Spring 연동 설정
    └── ReservationChangeSteps.java       # 예약 상태 변경 Step 정의
```

---

## 테스트 작성 가이드

### Step 클래스 작성 시 규칙

1. **CucumberSpringConfiguration 상속**
   ```java
   public class MySteps extends CucumberSpringConfiguration { }
   ```

2. **ScenarioContext로 상태 관리**
   - 로컬 변수 대신 ScenarioContext 사용
   - 여러 Step 클래스 간 데이터 공유 가능


3. **TestDataFactory로 데이터 생성**
   - `findById(1L)` 같은 하드코딩 금지
   - 테스트마다 새 데이터 생성


4. **비즈니스 언어 사용**
   - Feature 파일: "예약은 취소 상태다" (O)
   - Feature 파일: "상태코드 200이 반환된다" (X)
   - 기술적 검증은 Step Definition 내부에서 처리