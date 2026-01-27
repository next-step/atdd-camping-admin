# 신규 기능: 캠핑사이트 가격 관리

## 배경

현재 예약 매출 계산 시 1박 요금이 `50000`으로 하드코딩되어 있습니다.

```java
// Reservation.java
public BigDecimal calculateRevenue() {
    return BigDecimal.valueOf(calculateNights())
                     .multiply(new BigDecimal("50000"));  // 하드코딩
}
```

## 문제점

1. 캠핑사이트마다 가격이 다를 수 없음
2. 가격 변경 시 과거 예약의 매출 계산도 영향받음
3. 시즌별/주말 요금 등 유연한 가격 정책 불가

## 제안하는 구조

```
┌─────────────────────────────────────────────────────────────┐
│  예약 생성 시 흐름                                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Campsite                      Reservation                  │
│  ┌──────────────────┐          ┌──────────────────┐        │
│  │ pricePerNight    │ ──복사──▶ │ pricePerNight    │        │
│  │ (현재 가격)        │          │ (예약 시점 가격)    │        │
│  └──────────────────┘          └──────────────────┘        │
│                                                             │
│  나중에 Campsite 가격 변경해도 기존 예약은 영향 없음           │
└─────────────────────────────────────────────────────────────┘
```

## 구현 범위

### 1. Campsite 엔티티 수정

```java
// Campsite.java
@Column(nullable = false)
private BigDecimal pricePerNight;  // 사이트별 1박 기본 요금
```

### 2. Reservation 엔티티 수정

```java
// Reservation.java
@Column(nullable = false)
private BigDecimal pricePerNight;  // 예약 시점의 1박 요금 (스냅샷)

public BigDecimal calculateRevenue() {
    return BigDecimal.valueOf(calculateNights())
                     .multiply(pricePerNight);  // 저장된 가격 사용
}
```

### 3. 예약 생성 로직 수정

```java
// ReservationService.java (예약 생성 시)
Reservation reservation = new Reservation(...);
reservation.setPricePerNight(campsite.getPricePerNight());  // 가격 복사
```

### 4. DB 스키마 변경

```sql
-- campsites 테이블
ALTER TABLE campsites ADD COLUMN price_per_night DECIMAL(10,2) NOT NULL DEFAULT 50000;

-- reservations 테이블
ALTER TABLE reservations ADD COLUMN price_per_night DECIMAL(10,2) NOT NULL DEFAULT 50000;
```

### 5. data.sql 초기 데이터 수정

```sql
-- 캠핑사이트별 가격 설정
INSERT INTO campsites (site_number, description, max_people, price_per_night)
VALUES ('A-01', '숲속 사이트', 4, 50000);
INSERT INTO campsites (site_number, description, max_people, price_per_night)
VALUES ('A-02', '호수 전망 사이트', 6, 70000);
```

## 인수 테스트 시나리오 (안)

```gherkin
# features/campsite/price.feature

Feature: 캠핑사이트 가격 관리

  Scenario: 캠핑사이트에 1박 요금 설정
    Given 관리자로 로그인 되어 있다
    When 사이트번호 "B-01", 설명 "프리미엄 사이트", 최대인원 4, 1박요금 80000으로 캠핑사이트를 생성하면
    Then 캠핑사이트가 생성된다
    And 1박 요금이 80000원이다

  Scenario: 예약 시 가격 스냅샷 저장
    Given 관리자로 로그인 되어 있다
    And 1박 요금이 50000원인 캠핑사이트 "A-01"이 존재한다
    When 해당 사이트로 2박 예약을 생성하면
    Then 예약의 총 금액은 100000원이다
    When 캠핑사이트 "A-01"의 1박 요금을 60000원으로 변경하면
    Then 기존 예약의 총 금액은 여전히 100000원이다
```

## 예상 작업량

- [ ] Campsite 엔티티 수정
- [ ] Reservation 엔티티 수정
- [ ] 예약 생성 서비스 로직 수정
- [ ] data.sql 수정
- [ ] 인수 테스트 작성
- [ ] 기존 테스트 수정

## 참고

이 기능은 리팩터링 중 발견된 개선 포인트입니다.
현재는 기존 동작을 유지하면서 `50000`이 `Reservation.calculateRevenue()`에 상수로 존재합니다.