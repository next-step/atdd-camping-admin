# 신규 기능 개발

## 구현 완료

### 예약 확인 코드 조회

고객이 예약 확인 코드로 자신의 예약 정보를 조회할 수 있는 기능.

**API**: `GET /api/reservations/lookup?code={confirmationCode}`

**구현 파일**:
- `ReservationRepository.findByConfirmationCode()`
- `ReservationService.findByConfirmationCode()`
- `ReservationAdminController.findByConfirmationCode()`
- `features/reservation/lookup.feature`

---

## TODO: 추가 개발 예정 기능

### 1. 재고 부족 상품 조회

**목적**: 재고가 특정 임계값 이하인 상품을 한눈에 파악하여 재주문 계획 수립

**시나리오 (안)**:
```gherkin
Feature: 재고 부족 상품 조회

  Scenario: 재고가 부족한 상품 목록 조회
    Given 관리자로 로그인 되어 있다
    When 관리자가 재고 5개 이하 상품을 조회한다
    Then 재고 부족 상품 목록이 반환된다
```

**구현 요소**:
- `GET /admin/products/low-stock?maxStock={threshold}` API
- `ProductRepository.findByStockLessThanEqual()` 쿼리
- `ProductService.findLowStock()` 메서드

---

### 2. 판매 취소

**목적**: 잘못된 판매 기록을 취소하고 재고를 복원

**시나리오 (안)**:
```gherkin
Feature: 판매 취소

  Scenario: 판매 기록 취소
    Given 관리자로 로그인 되어 있다
    And 판매 기록이 있다
    When 관리자가 해당 판매를 취소한다
    Then 판매가 취소된다
    And 상품 재고가 복원된다
```

**구현 요소**:
- `DELETE /api/sales/{id}` API
- `SalesRecord.cancel()` 메서드 (isCancelled 플래그, 재고 복원)
- `SalesService.cancelSale()` 메서드

---

### 3. 캠핑사이트 가격 관리

**목적**: 하드코딩된 1박 요금(50000원)을 캠핑사이트별로 설정 가능하게 변경

**현재 문제점**:
- 캠핑사이트마다 가격이 다를 수 없음
- 가격 변경 시 과거 예약의 매출 계산도 영향받음
- 시즌별/주말 요금 등 유연한 가격 정책 불가

**제안 구조**:
- Campsite에 `pricePerNight` 필드 추가
- 예약 생성 시 해당 시점의 가격을 Reservation에 복사 (스냅샷)
- 이후 Campsite 가격이 변경되어도 기존 예약은 영향 없음

**시나리오 (안)**:
```gherkin
Feature: 캠핑사이트 가격 관리

  Scenario: 캠핑사이트에 1박 요금 설정
    Given 관리자로 로그인 되어 있다
    When 사이트번호 "B-01", 1박요금 80000으로 캠핑사이트를 생성하면
    Then 캠핑사이트가 생성된다
    And 1박 요금이 80000원이다

  Scenario: 예약 시 가격 스냅샷 저장
    Given 1박 요금이 50000원인 캠핑사이트가 존재한다
    When 해당 사이트로 2박 예약을 생성하면
    Then 예약의 총 금액은 100000원이다
    When 캠핑사이트의 1박 요금을 60000원으로 변경하면
    Then 기존 예약의 총 금액은 여전히 100000원이다
```
