# ATDD 캠핑 관리자 시스템 - 프로젝트 분석서

> E2E 인수테스트를 위한 프로젝트 참고 문서

---

## 1. API 엔드포인트 전체 목록

### 1.1 인증 API (AuthController)

| Method | Endpoint | Request | Response | 기능 |
|--------|----------|---------|----------|------|
| POST | `/auth/login` | `{ "username", "password" }` | `{ "token" }` + 쿠키 | 관리자 로그인, JWT 발급 |

### 1.2 예약 관리 API (ReservationAdminController)

| Method | Endpoint | Request | Response | 기능 |
|--------|----------|---------|----------|------|
| GET | `/admin/reservations` | - | `List<ReservationResponse>` | 전체 예약 조회 |
| PATCH | `/admin/reservations/{id}/status` | `{ "status" }` | `ReservationResponse` | 예약 상태 변경 |

### 1.3 캠프사이트 관리 API (CampsiteAdminController)

| Method | Endpoint | Request | Response | 기능 |
|--------|----------|---------|----------|------|
| GET | `/admin/campsites` | - | `List<Campsite>` | 전체 캠프사이트 조회 |
| POST | `/admin/campsites` | `{ "siteNumber", "description", "maxPeople" }` | `Campsite` | 캠프사이트 생성 |
| PUT | `/admin/campsites/{id}` | `{ "siteNumber", "description", "maxPeople" }` | `Campsite` | 캠프사이트 수정 |

### 1.4 상품 관리 API (ProductAdminController)

| Method | Endpoint | Request | Response | 기능 |
|--------|----------|---------|----------|------|
| GET | `/admin/products` | - | `List<Product>` | 전체 상품 조회 |
| POST | `/admin/products` | `{ "name", "stockQuantity", "price", "productType" }` | `Product` | 상품 생성 |
| PUT | `/admin/products/{id}` | `{ "name", "stockQuantity", "price", "productType" }` | `Product` | 상품 수정 |

### 1.5 대여 관리 API (RentalAdminController)

| Method | Endpoint | Request | Response | 기능 |
|--------|----------|---------|----------|------|
| GET | `/admin/rentals` | - | `List<RentalResponse>` | 전체 대여 기록 조회 |
| POST | `/admin/rentals` | `{ "productId", "quantity", "reservationId" }` | `RentalResponse` | 대여 생성 |
| PATCH | `/admin/rentals/{id}/return` | - | `RentalResponse` | 반납 처리 |

### 1.6 판매 API (SalesController)

| Method | Endpoint | Request | Response | 기능 |
|--------|----------|---------|----------|------|
| GET | `/api/sales` | - | `List<SalesRecordResponse>` | 최근 10건 판매 조회 |
| POST | `/api/sales` | `{ "items": [{ "productId", "quantity" }] }` | - | 판매 처리 |

### 1.7 매출 리포트 API (RevenueAdminController)

| Method | Endpoint | Query Params | Response | 기능 |
|--------|----------|--------------|----------|------|
| GET | `/admin/reports/revenue/daily` | `date` | `DailyRevenueReportResponse` | 일별 매출 리포트 |
| GET | `/admin/reports/revenue/range` | `from`, `to` | `RangeRevenueReportResponse` | 기간별 매출 합계 |
| GET | `/admin/reports/revenue/range/entries` | `from`, `to` | `List<RevenueEntryResponse>` | 기간별 상세 항목 |

**총 20개 API 엔드포인트** (관리자 API: `/admin/**`, 공개 API: `/api/**`)

---

## 2. 핵심 비즈니스 규칙

### 2.1 상품 재고 관리 (ProductService)

| 규칙 | 설명 | 예외 |
|------|------|------|
| 재고 검증 | 감소 시 요청 수량 ≤ 현재 재고 | `IllegalStateException` |
| 상품 존재 | 상품 ID로 조회 시 필수 존재 | `IllegalArgumentException` |
| 재고 증가 | 검증 없이 증가 허용 | - |

```java
// 재고 부족 검증
if (product.getStockQuantity() < quantity) {
    throw new IllegalStateException("Not enough stock for product " + product.getName());
}
```

### 2.2 대여 관리 (RentalService)

| 규칙 | 설명 | 예외 |
|------|------|------|
| 상품 타입 | `RENTAL` 타입만 대여 가능 | `IllegalArgumentException` |
| 중복 반납 방지 | 이미 반납된 상품 재반납 불가 | `IllegalStateException` |
| 예약 연결 | `reservationId`는 선택사항 (워크인 대여) | - |
| 재고 연동 | 대여 시 감소, 반납 시 복구 | - |

```java
// 상품 타입 검증
if (product.getProductType() != ProductType.RENTAL) {
    throw new IllegalArgumentException("Product is not a rental item.");
}

// 중복 반납 검증
if (rentalRecord.getIsReturned()) {
    throw new IllegalStateException("This item has already been returned.");
}
```

### 2.3 판매 처리 (SalesService)

| 규칙 | 설명 | 예외 |
|------|------|------|
| 상품 존재 | 판매 항목의 상품 ID 필수 존재 | `IllegalArgumentException` |
| 가격 계산 | 상품 가격 × 수량 = 총 가격 | - |
| 재고 차감 | 판매 시 자동 재고 감소 | `IllegalStateException` |

### 2.4 매출 계산 규칙

| 수익 유형 | 계산 방식 |
|----------|----------|
| 판매 수익 | `SalesRecord.totalPrice` 합계 |
| 예약 수익 | `(endDate - startDate) × 50,000원` (최소 1박) |
| 대여 수익 | `Product.price × RentalRecord.quantity` |
| 총 수익 | 판매 + 예약 + 대여 |

```java
// 최소 1박 보장
if (nights < 1) nights = 1;
return new BigDecimal(nights).multiply(new BigDecimal("50000"));
```

### 2.5 트랜잭션 전략

| 레이어 | 기본 설정 | 쓰기 작업 |
|--------|----------|----------|
| Service 클래스 | `@Transactional(readOnly = true)` | 메서드별 `@Transactional` |

---

## 3. 잠재적 버그 및 코드 스멜

### 3.1 동시성 문제 (Race Condition) - **심각도: 높음**

#### ProductService.java:18-22 - 재고 감소
```java
@Transactional
public void decreaseStock(Long productId, Integer quantity) {
    Product product = findById(productId);
    if (product.getStockQuantity() < quantity) {  // Read
        throw new IllegalStateException("Not enough stock...");
    }
    product.setStockQuantity(product.getStockQuantity() - quantity);  // Write
}
```

**문제점:**
- Read-Check-Write 패턴의 전형적인 race condition
- 두 스레드가 동시에 같은 재고를 감소시킬 수 있음
- 트랜잭션 격리 수준만으로는 해결 불가

**개선 방안:**
```java
// 비관적 락
@Lock(LockModeType.PESSIMISTIC_WRITE)
Product findByIdWithLock(Long id);

// 또는 원자적 UPDATE 쿼리
@Modifying
@Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :qty WHERE p.id = :id AND p.stockQuantity >= :qty")
int decreaseStockAtomically(@Param("id") Long id, @Param("qty") Integer qty);
```

#### RentalService.java:61-64 - 반납 처리
```java
if (rentalRecord.getIsReturned()) {  // Read
    throw new IllegalStateException("This item has already been returned.");
}
rentalRecord.setReturned(true);  // Write
```

**문제점:** 두 요청이 동시에 같은 대여 기록을 반납 처리할 수 있음

---

### 3.2 보안 취약점 - **심각도: 높음**

#### AuthController.java:37 - 평문 비밀번호 비교
```java
if (adminUsername.equals(request.getUsername()) &&
    adminPassword.equals(request.getPassword())) {
```

**문제점:**
- 설정 파일에 평문 비밀번호 저장
- 메모리 덤프에서 노출 가능

**개선 방안:** Spring Security `PasswordEncoder` 사용

#### JwtAuthFilter.java:93 - JSON Injection
```java
String body = "{\"error\":\"" + message + "\"}";
```

**문제점:** message에 `"` 포함 시 JSON 구조 깨짐

**개선 방안:** `ObjectMapper` 사용

---

### 3.3 N+1 쿼리 문제 - **심각도: 높음**

#### SalesService.java:75 - 전체 데이터 로드
```java
BigDecimal totalRentalRevenue = rentalRecordRepository.findAll().stream()
    .filter(rr -> rr.getCreatedAt().toLocalDate().equals(date))
    .map(rr -> rr.getProduct().getPrice().multiply(...))  // Lazy Loading!
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**문제점:**
- 모든 대여 기록을 메모리에 로드
- 각 레코드마다 Product 추가 쿼리 발생 (N+1)
- 데이터 증가 시 OOM 위험

**개선 방안:**
```java
@Query("SELECT SUM(r.quantity * r.product.price) FROM RentalRecord r WHERE DATE(r.createdAt) = :date")
BigDecimal calculateRentalRevenueByDate(@Param("date") LocalDate date);
```

#### ReservationResponse.java:28 - Lazy Loading
```java
this.campsiteSiteNumber = reservation.getCampsite().getSiteNumber();
```

**문제점:** Campsite가 LAZY 설정, DTO 변환 시 N+1 발생

---

### 3.4 Null 처리 미흡

#### ReservationAdminController.java:64 - 상태값 검증 없음
```java
String statusValue = statusObj.toString();
reservation.setStatus(statusValue);  // 어떤 문자열이든 저장됨
```

**문제점:**
- `ReservationStatus` enum 존재하지만 사용 안 함
- 잘못된 상태값 저장 가능

**개선 방안:**
```java
try {
    ReservationStatus.valueOf(statusValue);  // 검증
    reservation.setStatus(statusValue);
} catch (IllegalArgumentException e) {
    return ResponseEntity.badRequest().body("Invalid status");
}
```

#### Reservation.java:39 - String 타입 상태
```java
private String status;  // enum 대신 String 사용
```

**개선 방안:**
```java
@Enumerated(EnumType.STRING)
private ReservationStatus status;
```

---

### 3.5 불필요한 코드 (Code Smell)

#### ReservationAdminController.java:29-40
```java
List<ReservationResponse> all = reservationRepository.findAll().stream()...;

List<ReservationResponse> result = new ArrayList<>();
if (all == null) {           // collect()는 절대 null 반환 안 함
    // null이면 빈 리스트 반환
} else if (all.isEmpty()) {
    // 그대로 빈 리스트 반환
} else {
    for (ReservationResponse r : all) {
        if (r != null) {     // 불필요한 검증
            result.add(r);
        }
    }
}
```

**개선 방안:**
```java
return ResponseEntity.ok(
    reservationRepository.findAll().stream()
        .map(ReservationResponse::from)
        .collect(Collectors.toList())
);
```

#### CampsiteAdminController.java:20-37 - 동일 패턴
```java
if (all == null) { ... }  // findAll()은 null 반환 안 함
```

---

### 3.6 하드코딩된 값

#### SalesService.java:69,101,145 - 예약 가격
```java
return new BigDecimal(nights).multiply(new BigDecimal("50000"));
```

**개선 방안:**
```java
@Value("${campsite.price-per-night:50000}")
private BigDecimal pricePerNight;
```

#### JwtService.java:35 - 역할 하드코딩
```java
.claim("role", "ADMIN")
```

---

### 3.7 예외 처리 문제

#### CampsiteAdminController.java:93 - 부적절한 예외 타입
```java
.orElseThrow(() -> new IllegalArgumentException("Cannot find campsite..."));
```

**문제점:** 리소스 미존재는 `EntityNotFoundException` (404)이 적절

#### 빈 catch 블록
```java
} catch (Exception ignore) {}  // 예외 무시, 디버깅 어려움
```

---

### 3.8 코드 중복 (DRY 위반)

#### ProductAdminController.java:36-103 - 파라미터 파싱
```java
String name;
if (body.containsKey("name")) {
    Object v = body.get("name");
    name = v == null ? null : v.toString();
} else {
    name = null;
}
// 같은 패턴이 4번 반복
```

**개선 방안:** DTO 클래스 사용 또는 헬퍼 메서드 추출

#### SalesService.java:84-111 - 날짜 필터링
```java
.filter(record -> {
    LocalDate d = record.getCreatedAt().toLocalDate();
    return (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to));
})
// 3번 반복 (판매, 예약, 대여)
```

---

## 4. 문제 요약 테이블

| 심각도 | 파일 | 라인 | 문제 유형 | 설명 |
|--------|------|------|----------|------|
| **높음** | ProductService | 18-22 | 동시성 | 재고 감소 race condition |
| **높음** | AuthController | 37 | 보안 | 평문 비밀번호 비교 |
| **높음** | SalesService | 75 | 성능 | N+1 쿼리 + OOM 위험 |
| **중간** | RentalService | 61-64 | 동시성 | 반납 처리 race condition |
| **중간** | JwtAuthFilter | 93 | 보안 | JSON Injection |
| **중간** | ReservationAdminController | 64 | 검증 | 상태값 검증 없음 |
| **중간** | Reservation | 39 | 설계 | String 타입 상태 필드 |
| **중간** | SalesService | 69,101,145 | 유지보수 | 하드코딩된 가격 |
| **낮음** | ReservationAdminController | 29-40 | 코드스멜 | 불필요한 null 체크 |
| **낮음** | ProductAdminController | 36-103 | 코드스멜 | 코드 중복 |
| **낮음** | CampsiteAdminController | 93 | 예외처리 | 부적절한 예외 타입 |

---

## 5. 권장 개선 우선순위

1. **즉시 수정 필요**
   - ProductService 재고 관리 동시성 문제 (비관적 락 또는 원자적 쿼리)
   - 인증 시스템 보안 강화 (PasswordEncoder)

2. **단기 개선**
   - N+1 쿼리 최적화 (JPQL 집계 쿼리)
   - 상태값 검증 로직 추가 (enum 활용)

3. **중기 개선**
   - 하드코딩 값 설정 파일로 이동
   - 불필요한 코드 제거
   - DTO 클래스 도입으로 중복 제거

4. **장기 개선**
   - 전반적인 예외 처리 체계 정립
   - 글로벌 예외 핸들러 추가
