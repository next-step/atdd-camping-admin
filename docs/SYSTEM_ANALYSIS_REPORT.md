# 캠핑 관리자 시스템 분석 보고서

## 목차
1. [프로젝트 개요](#1-프로젝트-개요)
2. [핵심 비즈니스 규칙](#2-핵심-비즈니스-규칙)
3. [잠재적 버그 및 코드 스멜](#3-잠재적-버그-및-코드-스멜)
4. [예외 및 엣지케이스](#4-예외-및-엣지케이스)
5. [기능별 상세 분석](#5-기능별-상세-분석)

---

## 1. 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | Camping Admin Service (ATDD) |
| 프레임워크 | Spring Boot 3.2.0, Java 17 |
| 데이터베이스 | H2 (In-Memory, auto create-drop) |
| 테스트 | Cucumber + RestAssured + JUnit Platform |
| 인증 | JWT (jjwt 0.11.5) |

### 분석 대상 API
- **예약 관리**: `PATCH /admin/reservations/{id}/status`, `GET /admin/reservations`
- **상품 관리**: `POST /admin/products`
- **캠프사이트 관리**: `POST /admin/campsites`
- **대여 관리**: `POST /admin/rentals`

---

## 2. 핵심 비즈니스 규칙

### 2.1 예약 (Reservation)

| 규칙 | 설명 | 위치 |
|------|------|------|
| 기본 상태 | 예약 생성 시 기본 상태는 `CONFIRMED` | `Reservation.java:@PrePersist` |
| 중복 예약 방지 | 동일 캠프사이트에 날짜가 겹치는 예약 불가 (취소된 예약 제외) | `ReservationRepository.findOverlappingReservations()` |
| 상태 값 | `WAITING`, `PENDING`, `CONFIRMED`, `REJECTED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED` | `ReservationStatus.java` |
| 필수 필드 | customerName, startDate, endDate, campsite | Entity 제약조건 |

### 2.2 상품 (Product)

| 규칙 | 설명 | 위치 |
|------|------|------|
| 상품 유형 | `SALE` (판매용) 또는 `RENTAL` (대여용) | `ProductType.java` |
| 기본값 | stockQuantity=0, price=0.00, productType=SALE | `ProductAdminController.java` |
| 재고 관리 | 재고는 0 이상이어야 함 | `ProductService.decreaseStock()` |
| 가격 정밀도 | precision=10, scale=2 (소수점 2자리) | `Product.java` |

### 2.3 캠프사이트 (Campsite)

| 규칙 | 설명 | 위치 |
|------|------|------|
| 고유 식별자 | siteNumber는 시스템 전체에서 유일해야 함 | `@Column(unique=true)` |
| 연관 관계 | 캠프사이트 삭제 시 연관 예약도 함께 삭제 | `CascadeType.ALL` |
| 필수 필드 | siteNumber (NOT NULL, UNIQUE) | Entity 제약조건 |

### 2.4 대여 (Rental)

| 규칙 | 설명 | 위치 |
|------|------|------|
| 상품 타입 검증 | RENTAL 타입 상품만 대여 가능 | `RentalService.createRental()` |
| 재고 차감 | 대여 시 상품 재고에서 차감 | `ProductService.decreaseStock()` |
| 재고 복구 | 반납 시 상품 재고에 복구 | `ProductService.increaseStock()` |
| 이중 반납 방지 | 이미 반납된 항목은 재반납 불가 | `RentalService.markAsReturned()` |
| 워크인 지원 | reservationId 없이 대여 가능 (워크인 고객) | `RentalRecord.reservation` nullable |

### 2.5 매출 (Sales/Revenue)

| 규칙 | 설명 | 위치 |
|------|------|------|
| 숙박 요금 | 1박당 50,000원 (하드코딩) | `SalesService.java` |
| 매출 집계 | 예약/판매/대여 별도 집계 | `SalesService.generateDailyRevenueReport()` |

---

## 3. 잠재적 버그 및 코드 스멜

### 3.1 심각도 높음 (Critical)

#### 3.1.1 예약 상태 검증 누락
```java
// ReservationAdminController.java:44-70
reservation.setStatus(statusValue); // 어떤 문자열이든 저장 가능
```
**문제점**: ReservationStatus enum이 정의되어 있으나, 실제 상태 변경 시 검증하지 않음
- 임의의 문자열(예: "INVALID_STATUS")이 저장될 수 있음
- 상태 전이 규칙(State Machine)이 없어 비정상적 전이 가능 (예: CANCELLED → CONFIRMED)

**예상 시나리오**:
```
PATCH /admin/reservations/1/status
Body: {"status": "ABCDEFG"}
→ 200 OK (검증 없이 저장됨)
```

#### 3.1.2 동시성 이슈 - 재고 관리
```java
// ProductService.java
if (product.getStockQuantity() < quantity) { // Read
    throw new IllegalStateException(...);
}
product.setStockQuantity(product.getStockQuantity() - quantity); // Write
```
**문제점**: Read-then-Write 패턴으로 Race Condition 발생 가능
- 두 요청이 동시에 재고 확인 후 차감하면 재고가 음수가 될 수 있음
- `@Lock(LockModeType.PESSIMISTIC_WRITE)` 또는 낙관적 락 필요

**예상 시나리오**:
```
Thread A: 재고=5, 대여 요청 수량=3 → 재고 확인(5≥3) 통과
Thread B: 재고=5, 대여 요청 수량=4 → 재고 확인(5≥4) 통과
Thread A: 재고=5-3=2 저장
Thread B: 재고=5-4=1 저장 (실제로는 -2가 되어야 함)
```

#### 3.1.3 동시성 이슈 - 예약 중복 검사
```java
// ReservationRepository.java
@Query("SELECT r FROM Reservation r WHERE r.campsite.id = :campsiteId " +
       "AND r.status != 'CANCELLED' " +
       "AND (r.startDate < :endDate AND r.endDate > :startDate)")
List<Reservation> findOverlappingReservations(...);
```
**문제점**: 조회와 저장 사이에 다른 예약이 들어올 수 있음
- 유니크 제약조건이나 락 없이 중복 예약 발생 가능

### 3.2 심각도 중간 (Medium)

#### 3.2.1 Null 안전성 미흡
```java
// ProductAdminController.java
Product newProduct = new Product(name, stockQuantity, price, productType);
// name이 null일 수 있으나 Entity에서 @Column(nullable=false)
```
**문제점**: Controller에서 null을 허용하지만 DB 제약조건으로 인해 저장 시 예외 발생

#### 3.2.2 저장 결과 null 체크 (안티패턴)
```java
// CampsiteAdminController.java, ProductAdminController.java
Product saved = productRepository.save(newProduct);
if (saved == null) {  // JPA save()는 null을 반환하지 않음
    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
}
```
**문제점**: `JpaRepository.save()`는 null을 반환하지 않음. 불필요한 코드.

#### 3.2.3 Map 기반 요청 파싱 (코드 스멜)
```java
// ProductAdminController.java, CampsiteAdminController.java
@PostMapping
public ResponseEntity<Product> createProduct(@RequestBody Map<String, Object> body) {
    Object v = body.get("name");
    name = v == null ? null : v.toString();
    // ... 수동 타입 변환 반복
}
```
**문제점**:
- 타입 안전성 없음
- 중복 코드 다수
- DTO 클래스와 `@Valid` 검증 사용이 더 적절

#### 3.2.4 입력 검증 부재
```java
// CreateRentalRequest.java - 검증 어노테이션 없음
private Long productId;    // null 가능
private Integer quantity;  // 음수, 0 가능
```
**문제점**:
- productId가 null이면 NullPointerException 발생
- quantity가 0이나 음수여도 처리됨

### 3.3 심각도 낮음 (Low)

#### 3.3.1 예외 처리 일관성 부재
**문제점**: `@ExceptionHandler`나 `@ControllerAdvice` 없음
- 예외 발생 시 Spring 기본 에러 응답 사용
- 클라이언트에게 일관된 에러 형식 제공 불가

#### 3.3.2 하드코딩된 비즈니스 값
```java
// SalesService.java
BigDecimal reservationRevenue = BigDecimal.valueOf(50000L).multiply(...);
```
**문제점**: 숙박 요금이 코드에 하드코딩됨. 설정 파일이나 DB로 관리 필요.

#### 3.3.3 페이징 없는 목록 조회
```java
// ReservationAdminController.java
return reservationRepository.findAll().stream()...
```
**문제점**: 데이터 증가 시 메모리 부족 및 응답 지연 가능

---

## 4. 예외 및 엣지케이스

### 4.1 예약 상태 변경 (`PATCH /admin/reservations/{id}/status`)

| 케이스 | 현재 동작 | 예상 문제 |
|--------|----------|----------|
| 존재하지 않는 예약 ID | `IllegalArgumentException` 발생 | 500 에러 (핸들러 없음) |
| 빈 요청 본문 `{}` | 400 BAD_REQUEST 반환 | 기존 예약 정보도 함께 반환됨 (비정상) |
| 잘못된 상태 값 | 그대로 저장됨 | 데이터 무결성 손상 |
| null 상태 값 `{"status": null}` | 변경 안 됨 | 응답만으로는 실패 여부 알 수 없음 |
| 빈 문자열 `{"status": ""}` | 변경 안 됨 | 동일 |
| 취소된 예약 재활성화 | 허용됨 | 비즈니스 규칙 위반 가능 |

### 4.2 상품 생성 (`POST /admin/products`)

| 케이스 | 현재 동작 | 예상 문제 |
|--------|----------|----------|
| name이 null | DB 저장 시 예외 | DataIntegrityViolationException → 500 |
| name이 빈 문자열 | 저장됨 | 의미 없는 데이터 |
| stockQuantity 음수 | 0으로 처리 | 입력 오류 숨김 |
| price 음수 | 저장됨 | 비정상 데이터 |
| 잘못된 productType | SALE로 처리 | 입력 오류 숨김 |
| stockQuantity 문자열 "abc" | 0으로 처리 | 조용한 실패 |

### 4.3 캠프사이트 생성 (`POST /admin/campsites`)

| 케이스 | 현재 동작 | 예상 문제 |
|--------|----------|----------|
| siteNumber가 null | DB 저장 시 예외 | 500 에러 |
| siteNumber 중복 | DB 예외 발생 | DataIntegrityViolationException → 500 |
| maxPeople가 0 또는 음수 | 저장됨 | 비정상 데이터 |
| description 매우 긴 문자열 | 저장됨 | DB 컬럼 제한에 따라 잘림 또는 예외 |

### 4.4 대여 생성 (`POST /admin/rentals`)

| 케이스 | 현재 동작 | 예상 문제 |
|--------|----------|----------|
| 존재하지 않는 productId | `IllegalArgumentException` | 500 에러 |
| SALE 타입 상품 대여 시도 | `IllegalArgumentException` | 500 에러 |
| 재고 부족 | `IllegalStateException` | 500 에러 |
| quantity가 0 | 재고 0 차감, 기록 생성 | 의미 없는 대여 기록 |
| quantity가 음수 | 재고 증가됨 | 재고 조작 취약점 |
| 존재하지 않는 reservationId | `IllegalArgumentException` | 500 에러 |
| 동시 대여 요청 (같은 상품) | 둘 다 성공 가능 | 재고 음수 가능 |

### 4.5 예약 목록 조회 (`GET /admin/reservations`)

| 케이스 | 현재 동작 | 예상 문제 |
|--------|----------|----------|
| 예약 없음 | 빈 배열 `[]` 반환 | 정상 |
| 수만 건 예약 | 모두 조회 | 메모리 부족, 응답 지연 |
| 연관 캠프사이트 삭제됨 | 조회 가능 (LAZY) | `LazyInitializationException` 가능 |

---

## 5. 기능별 상세 분석

### 5.1 예약 상태 변경 흐름

```
클라이언트 → Controller → Repository → DB
          ↓
    1. ID로 예약 조회
    2. 본문 검증 (빈 본문 체크)
    3. 상태 값 추출
    4. 상태 설정 (검증 없음)
    5. 저장
    6. 응답 반환
```

**개선 권장사항**:
- ReservationStatus enum 사용하여 유효성 검증
- 상태 전이 규칙 정의 (State Machine 패턴)
- `@ExceptionHandler`로 일관된 에러 응답

### 5.2 대여 생성 흐름

```
클라이언트 → Controller → RentalService → ProductService
                                      → RentalRecordRepository
                                      → ReservationRepository
          ↓
    1. 상품 조회 및 RENTAL 타입 검증
    2. 재고 차감 (동시성 이슈 가능)
    3. 예약 조회 (optional)
    4. RentalRecord 생성 및 저장
    5. 응답 반환
```

**개선 권장사항**:
- 재고 차감에 비관적 락 적용
- quantity 양수 검증 추가
- 트랜잭션 격리 수준 검토

### 5.3 상품 생성 흐름

```
클라이언트 → Controller → Repository → DB
          ↓
    1. Map에서 필드 추출 (수동 파싱)
    2. 타입 변환 (예외 시 기본값)
    3. Product 엔티티 생성
    4. 저장
    5. 응답 반환
```

**개선 권장사항**:
- DTO 클래스 도입 (`CreateProductRequest`)
- `@Valid`와 Bean Validation 사용
- 비즈니스 검증 로직 Service 계층으로 분리

---

## 부록: 권장 개선 우선순위

| 우선순위 | 항목 | 이유 |
|---------|------|------|
| 1 | 동시성 제어 (재고 관리) | 데이터 무결성 손상 가능 |
| 2 | 예약 상태 enum 검증 | 잘못된 데이터 유입 방지 |
| 3 | 입력 검증 추가 | 보안 및 데이터 품질 |
| 4 | 글로벌 예외 처리 | 일관된 API 응답 |
| 5 | DTO 기반 요청 처리 | 코드 품질 및 유지보수성 |
| 6 | 페이징 적용 | 확장성 |

---

*작성일: 2026-01-26*
*분석 대상: ATDD Camping Admin System*