# Gherkin의 3단계 추상화 모델

## 1. 연극 비유로 이해하기

| 연극의 요소 | Gherkin 요소 | 역할 |
|-------------|--------------|------|
| 스포트라이트 | **Scenario** | 주인공과 핵심 사건(What)에만 집중 |
| 무대 설정 | **Background** | 여러 시나리오가 공유하는 비즈니스 전제 |
| 백스테이지 | **Hooks** | 보이지 않는 기술 준비/정리 (DB 초기화 등) |

---

## 2. Scenario (스포트라이트)

### 정의
- 테스트하려는 **핵심 시나리오**
- Given/When/Then으로 구성
- **비즈니스 관점**에서 무엇을 검증하는지 명확히 표현

### 작성 위치
```
src/test/resources/features/reservation_cancel.feature
```

### 예시
```gherkin
Scenario: 사용자가 예약한 건을 관리자가 취소하면 성공한다
  Given 사용자가 캠핑장 예약을 했다
  When 관리자가 해당 예약을 취소하면
  Then 예약이 성공적으로 취소된다
```

---

## 3. Background (무대 설정)

### 정의
- 여러 시나리오가 **공유하는 비즈니스 전제 조건**
- 해당 Feature의 **모든 Scenario 전에** 자동 실행됨
- feature 파일 안에 작성 (눈에 보임)

### 작성 위치
```
src/test/resources/features/*.feature (feature 파일 안에)
```

### 문제 상황: 중복 발생
```gherkin
Feature: 관리자의 예약 관리

  Scenario: 관리자가 예약을 취소한다
    Given 관리자가 로그인했다          # 중복!
    And 사용자가 캠핑장 예약을 했다
    When 관리자가 해당 예약을 취소하면
    Then 예약이 성공적으로 취소된다

  Scenario: 관리자가 예약을 조회한다
    Given 관리자가 로그인했다          # 중복!
    And 사용자가 캠핑장 예약을 했다
    When 관리자가 예약 목록을 조회하면
    Then 예약 정보가 표시된다

  Scenario: 관리자가 예약을 수정한다
    Given 관리자가 로그인했다          # 중복!
    And 사용자가 캠핑장 예약을 했다
    When 관리자가 예약 날짜를 변경하면
    Then 예약 날짜가 변경된다
```

### 해결: Background 사용
```gherkin
Feature: 관리자의 예약 관리

  Background:
    Given 관리자가 로그인했다          # 모든 시나리오 전에 자동 실행!

  Scenario: 관리자가 예약을 취소한다
    Given 사용자가 캠핑장 예약을 했다
    When 관리자가 해당 예약을 취소하면
    Then 예약이 성공적으로 취소된다

  Scenario: 관리자가 예약을 조회한다
    Given 사용자가 캠핑장 예약을 했다
    When 관리자가 예약 목록을 조회하면
    Then 예약 정보가 표시된다
```

---

## 4. Hooks (백스테이지)

### 정의
- **기술적인 준비/정리** 작업
- Java 코드에만 존재 (feature 파일에서 안 보임)
- `@Before`, `@After` 어노테이션 사용

### 작성 위치
```
src/test/java/com/camping/admin/steps/Hooks.java (Java 파일)
```

### 예시
```java
import io.cucumber.java.Before;
import io.cucumber.java.After;

public class Hooks {

    @Before  // 매 시나리오 실행 전에 자동 실행
    public void setUp() {
        // 기술적인 준비 (비즈니스와 무관)
        DatabaseCleaner.clean();        // DB 초기화
        TestServer.start();             // 테스트 서버 시작
    }

    @After   // 매 시나리오 실행 후에 자동 실행
    public void tearDown() {
        // 기술적인 정리
        TestServer.stop();              // 테스트 서버 종료
    }
}
```

### Feature 파일에서는 안 보임!
```gherkin
# reservation_cancel.feature
# Hooks는 여기에 없음! 비즈니스 로직만 보임

Scenario: 관리자가 예약을 취소한다
  Given 사용자가 캠핑장 예약을 했다
  When 관리자가 해당 예약을 취소하면
  Then 예약이 성공적으로 취소된다
```

---

## 5. 실행 순서

```
[Hooks] @Before 실행 (DB 초기화, 서버 시작)
    ↓
[Background] Given 관리자가 로그인했다
    ↓
[Scenario] Given 사용자가 캠핑장 예약을 했다
[Scenario] When 관리자가 해당 예약을 취소하면
[Scenario] Then 예약이 성공적으로 취소된다
    ↓
[Hooks] @After 실행 (서버 종료)
```

---

## 6. 비교 정리

| 구분 | 어디에 작성? | 눈에 보임? | 언제 실행? | 용도 |
|------|-------------|-----------|-----------|------|
| **Scenario** | .feature 파일 | O | 명시적으로 | 핵심 시나리오 |
| **Background** | .feature 파일 | O | 각 Scenario 전 | 공유 비즈니스 전제 |
| **Hooks** | .java 파일 | X | 각 Scenario 전/후 | 기술적 setup/teardown |

---

## 7. Background의 한계와 해결책

### 문제: 모든 시나리오에 적용됨
```gherkin
Feature: 예약 관리

  Background:
    Given 관리자가 로그인했다    # 모든 시나리오에 적용됨

  Scenario: 관리자가 예약을 취소한다        # 관리자 로그인 필요 ✅
    ...

  Scenario: 비로그인 사용자가 예약 시도     # 로그인 하면 안됨! ❌
    ...
```

### 해결: Feature 파일 분리

**관리자 기능** (`admin_reservation.feature`)
```gherkin
Feature: 관리자의 예약 관리

  Background:
    Given 관리자가 로그인했다

  Scenario: 관리자가 예약을 취소한다
    ...

  Scenario: 관리자가 예약을 조회한다
    ...
```

**비로그인 기능** (`guest_reservation.feature`)
```gherkin
Feature: 비로그인 사용자의 예약 시도

  Scenario: 비로그인 사용자가 예약을 시도하면 실패한다
    Given 사용자가 로그인하지 않았다
    When 예약을 시도하면
    Then 로그인 페이지로 이동한다
```

### 원칙
> **하나의 Feature 파일 = 하나의 맥락(Context)**

| 상황 | 해결책 |
|------|--------|
| 모든 시나리오가 같은 전제 | Background 사용 |
| 일부만 같은 전제 | **Feature 파일 분리** |

---

## 8. 판단 기준 체크리스트

어떤 내용을 어디에 넣어야 할지 고민될 때:

```
Q: 이 스텝이 없으면 시나리오가 의미가 사라지는가?
A: Yes → Scenario에 넣기

Q: 다른 시나리오와 공유되는 비즈니스 전제인가?
A: Yes → Background에 넣기

Q: 비즈니스와 무관한 기술 준비인가?
A: Yes → Hooks에 넣기
```

### 예시로 판단해보기

| 내용 | 판단 | 이유 |
|------|------|------|
| "관리자가 로그인했다" | Background | 비즈니스 전제, 여러 시나리오 공유 |
| "DB 초기화" | Hooks | 기술적 준비, 비즈니스와 무관 |
| "예약을 취소하면" | Scenario | 핵심 동작, 이게 없으면 의미 없음 |
| "테스트 서버 시작" | Hooks | 기술적 준비, feature에 안 보여도 됨 |

---

## 9. 좋은 예 vs 나쁜 예

### 나쁜 예: 모든 것을 시나리오에 노출
```gherkin
Scenario: 재고 추가
  Given 테스트 DB가 초기화되어 있다           # 기술적 → Hooks로!
  And 관리자가 로그인했다                     # 공유 전제 → Background로!
  And "캠핑 의자"가 등록되어 있고 재고가 10개다
  When 재고를 5개 추가하면
  Then 재고는 15개가 된다
```

### 좋은 예: 역할에 맞게 분리
```java
// Hooks.java
@Before
public void setUp() {
    DatabaseCleaner.clean();  // 기술적 준비
}
```

```gherkin
# inventory.feature
Background:
  Given 관리자가 로그인했다   # 공유 비즈니스 전제

Scenario: 등록된 상품의 재고를 추가한다
  Given 재고가 10개인 "캠핑 의자" 상품이 등록되어 있다
  When 재고를 5개 추가하면
  Then 재고는 15개가 된다
```

---

## 10. 한 줄 요약

> **Scenario = 핵심만 (스포트라이트)**
> **Background = 공유 전제 (무대 설정)**
> **Hooks = 기술 준비 (백스테이지)**
