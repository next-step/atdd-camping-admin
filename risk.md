# Risk Analysis — 전체 API 버그 및 잠재적 문제점 분석

## 요약

전체 API 엔드포인트를 대상으로 확정 버그, 동시성/원자성/트랜잭션 문제, 입력 검증 누락, 성능 이슈를 분석한 결과이다.

| # | 대상 | 문제 유형 | 심각도 |
|---|---|---|---|
| 1 | `PUT /admin/campsites/{id}` | `.save()` 누락 → 수정 미반영 | **높음** |
| 2 | `PUT /admin/products/{id}` | `.save()` 누락 → 수정 미반영 | **높음** |
| 3 | `PATCH /admin/reservations/{id}/status` | 상태 검증/전이 규칙 없음 | **높음** |
| 4 | 재고 관리 (전체) | 동시성 제어 없음 (Race Condition) | **높음** |
| 5 | 전역 | `@ControllerAdvice` 없음 → 500 에러 노출 | **높음** |
| 6 | `POST /admin/campsites` | siteNumber null → DB 500 | 중간 |
| 7 | `POST /admin/products` | name null → DB 500 | 중간 |
| 8 | `POST /admin/rentals` | 수량 0/음수 미검증 | 중간 |
| 9 | 매출 리포트 (전체) | `findAll()` + stream 필터링 → 성능 | 중간 |
| 10 | `POST /api/sales` | 부분 실패 시 에러 메시지 부재 | 낮음 |

---

## 확정 버그

### 1. `PUT /admin/campsites/{id}` — `.save()` 누락

**파일:** `CampsiteAdminController.java:131`

엔티티 필드를 변경한 뒤 `repository.save()`를 호출하지 않는다. 컨트롤러에 `@Transactional`이 없으므로 JPA dirty checking이 동작하지 않아 변경 사항이 DB에 반영되지 않는다.

```java
// 필드 변경 후 save() 없이 바로 반환
campsite.setSiteNumber(v.toString());
// ...
return ResponseEntity.ok(campsite); // DB에 반영 안 됨
```

**재현:** 캠프사이트 수정 요청 후 목록 조회 시 변경 전 값이 반환된다.

---

### 2. `PUT /admin/products/{id}` — `.save()` 누락

**파일:** `ProductAdminController.java:153`

위와 동일한 문제. 상품 정보를 수정해도 DB에 반영되지 않는다.

```java
product.setName(v.toString());
// ...
return ResponseEntity.ok(product); // DB에 반영 안 됨
```

**재현:** 상품 가격을 수정 요청 후 목록 조회 시 변경 전 가격이 반환된다.

---

### 3. `PATCH /admin/reservations/{id}/status` — 상태 검증/전이 규칙 없음

**파일:** `ReservationAdminController.java:59-64`

요청으로 들어온 문자열을 검증 없이 그대로 `setStatus()`에 대입한다. `ReservationStatus` enum이 존재하지만 사용하지 않는다.

```java
String statusValue = statusObj.toString();
reservation.setStatus(statusValue); // "HELLO_WORLD"도 저장됨
```

**세부 문제:**
- 임의 문자열(`"HELLO_WORLD"`, `"12345"`) 저장 가능
- 상태 전이 규칙 없음 (`CANCELLED` → `CONFIRMED`, `CHECKED_OUT` → `WAITING`)
- `Reservation.status`가 `String` 타입 (enum 미사용)
- 빈 문자열/null 전달 시 에러 응답 없이 기존 값 유지 (무음 실패)

---

## 동시성 / 원자성 / 트랜잭션 문제

### 4. 재고 관리 — 동시성 제어 없음 (Race Condition)

**파일:** `ProductService.java:17-23`

`decreaseStock()`에서 read → check → write 패턴을 사용하지만 비관적 락(`@Lock(PESSIMISTIC_WRITE)`)이나 낙관적 락(`@Version`)이 전혀 없다.

```java
@Transactional
public void decreaseStock(Long productId, Integer quantity) {
    Product product = findById(productId);           // 1. READ
    if (product.getStockQuantity() < quantity) {      // 2. CHECK
        throw new IllegalStateException("Not enough stock");
    }
    product.setStockQuantity(product.getStockQuantity() - quantity);  // 3. WRITE
}
```

**시나리오:** 재고 20개인 상품에 두 요청이 동시에 10개씩 차감 시도

| 시점 | 요청 A | 요청 B |
|---|---|---|
| T1 | `read → 20` | `read → 20` |
| T2 | `check: 20 >= 10 ✓` | `check: 20 >= 10 ✓` |
| T3 | `write: 20 - 10 = 10` | `write: 20 - 10 = 10` |
| 결과 | **재고 10** (기대값: 0) | Lost Update 발생 |

**영향 범위:** `POST /admin/rentals`, `PATCH /admin/rentals/{id}/return`, `POST /api/sales`

---

### 5. `POST /api/sales` — 다건 판매 시 트랜잭션 내 순서 문제

**파일:** `SalesService.java:37-47`

for문에서 `decreaseStock()`을 먼저 호출한 뒤 같은 product를 다시 `findById()`로 조회한다. `decreaseStock()`이 자체 `@Transactional`로 선언되어 있으나, 기본 전파 속성(REQUIRED)으로 부모 트랜잭션에 참여하므로 현재는 정상 동작한다. 다만 전파 속성이 변경되면 재고 감소된 값이 아닌 이전 값을 읽을 수 있다.

```java
productService.decreaseStock(itemDto.getProductId(), itemDto.getQuantity());  // 재고 감소

Product product = productRepository.findById(itemDto.getProductId())  // 다시 조회
        .orElseThrow(...);

BigDecimal totalPrice = product.getPrice().multiply(...);  // 가격 계산
```

---

## 입력 검증 누락

### 6. `POST /admin/campsites` — siteNumber null 허용

**파일:** `CampsiteAdminController.java:41-87`

`siteNumber`를 전달하지 않으면 null로 `Campsite`를 생성 시도한다. `Campsite.siteNumber`는 `@Column(nullable = false, unique = true)`이므로 **DB 제약조건 위반 → 500 에러** 발생.

```java
siteNumber = null;  // body에 siteNumber 없을 때
// ...
Campsite newCampsite = new Campsite(siteNumber, description, maxPeople);
campsiteRepository.save(newCampsite);  // → DataIntegrityViolationException (500)
```

---

### 7. `POST /admin/products` — name null 허용

**파일:** `ProductAdminController.java:37-103`

`name`을 전달하지 않으면 null로 `Product`를 생성 시도한다. `Product.name`은 `@Column(nullable = false)`이므로 **DB 에러 500** 발생.

---

### 8. `POST /admin/rentals` — 수량 0/음수 미검증

**파일:** `RentalService.java:34-53`

`quantity`에 대한 양수 검증이 없다. 0을 전달하면 의미 없는 대여 기록이 생성되고, 음수를 전달하면 `decreaseStock()`에서 **재고가 오히려 증가**한다.

```java
// quantity = -5 전달 시
product.setStockQuantity(product.getStockQuantity() - (-5));  // 재고 증가!
```

---

## 성능 문제

### 9. 매출 리포트 — 전체 테이블 스캔 후 Java 필터링

**파일:** `SalesService.java:58-164`

`generateDailyRevenueReport()`, `generateRangeRevenueReport()`, `generateRangeRevenueEntries()` 모두 3개 테이블(`sales_records`, `reservations`, `rental_records`)에 대해 `findAll()`을 호출한 뒤 Java stream으로 날짜 필터링한다.

```java
BigDecimal totalSalesRevenue = salesRecordRepository.findAll().stream()  // 전체 로드
        .filter(record -> record.getCreatedAt().toLocalDate().equals(date))  // Java에서 필터
        .map(SalesRecord::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
```

데이터 증가 시 메모리 사용량과 응답 시간이 선형적으로 증가한다. DB 쿼리로 날짜 조건을 처리해야 한다.

---

## 기타

### 10. 전역 예외 처리 없음

프로젝트에 `@ControllerAdvice` / `@ExceptionHandler`가 존재하지 않는다. 서비스/리포지토리에서 발생하는 예외가 클라이언트에 Spring 기본 에러 응답(500)으로 전달된다.

| 예외 | 발생 위치 | 기대 응답 | 실제 응답 |
|---|---|---|---|
| `IllegalArgumentException("Cannot find...")` | 존재하지 않는 ID 조회 | 404 | **500** |
| `IllegalStateException("Not enough stock")` | 재고 부족 | 400 | **500** |
| `DataIntegrityViolationException` | null 필수 필드 | 400 | **500** |

---

## ATDD 보호 우선순위

1. **예약 상태 변경** (#3) — 이미 feature 시나리오 작성 완료
2. **캠프사이트 수정** (#1) — `.save()` 누락 확인 테스트
3. **상품 수정** (#2) — `.save()` 누락 확인 테스트
4. **재고 동시성** (#4) — 동시 요청 시 재고 정합성 테스트
5. **입력 검증** (#6, #7, #8) — 잘못된 입력 시 적절한 에러 응답 테스트
