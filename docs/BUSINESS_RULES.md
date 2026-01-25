# 비즈니스 규칙

> 캠핑장 관리 시스템의 핵심 비즈니스 로직

---

## 1. 상품 재고 관리

**위치**: `ProductService.java`

### 규칙

| 규칙 | 조건 | 실패 시 |
|------|------|---------|
| 재고 감소 | 요청 수량 <= 현재 재고 | `IllegalStateException` |
| 상품 조회 | ID에 해당하는 상품 존재 | `IllegalArgumentException` |
| 재고 증가 | 제한 없음 | - |

### 예시

```java
// 재고 부족 시 예외
if (product.getStockQuantity() < quantity) {
    throw new IllegalStateException("Not enough stock for product " + product.getName());
}
```

---

## 2. 대여 관리

**위치**: `RentalService.java`

### 규칙

| 규칙 | 설명 | 실패 시 |
|------|------|---------|
| 상품 타입 제한 | `RENTAL` 타입만 대여 가능 | `IllegalArgumentException` |
| 중복 반납 방지 | 이미 반납된 상품은 재반납 불가 | `IllegalStateException` |
| 예약 연결 | `reservationId`는 선택사항 | - |
| 재고 연동 | 대여 시 감소, 반납 시 복구 | - |

### 플로우

```
대여 생성:
1. 상품 타입 검증 (RENTAL인지)
2. 재고 확인 및 감소
3. 대여 기록 생성

반납 처리:
1. 반납 여부 확인
2. 재고 복구
3. 반납 상태 변경
```

---

## 3. 판매 처리

**위치**: `SalesService.java`

### 규칙

| 규칙 | 설명 | 실패 시 |
|------|------|---------|
| 상품 존재 | 판매 항목의 상품 ID 필수 존재 | `IllegalArgumentException` |
| 가격 계산 | `상품 가격 x 수량 = 총 가격` | - |
| 재고 차감 | 판매 시 자동 재고 감소 | `IllegalStateException` |

---

## 4. 매출 계산

**위치**: `SalesService.java`

### 수익 유형별 계산

| 유형 | 계산 방식 |
|------|----------|
| 판매 수익 | `SalesRecord.totalPrice` 합계 |
| 예약 수익 | `(endDate - startDate) x 50,000원` |
| 대여 수익 | `Product.price x RentalRecord.quantity` |
| **총 수익** | 판매 + 예약 + 대여 |

### 예약 수익 계산 규칙

```java
// 최소 1박 보장
long nights = ChronoUnit.DAYS.between(startDate, endDate);
if (nights < 1) nights = 1;
return new BigDecimal(nights).multiply(new BigDecimal("50000"));
```

- 1박 요금: **50,000원** (하드코딩)
- 당일 체크인/체크아웃도 최소 1박으로 계산

---

## 5. 예약 상태 관리

**위치**: `Reservation.java`, `ReservationStatus.java`

### 상태 종류

| 상태 | 설명 |
|------|------|
| `PENDING` | 예약 대기 |
| `CONFIRMED` | 예약 확정 |
| `CANCELLED` | 예약 취소 |
| `COMPLETED` | 이용 완료 |

### 주의사항

현재 `Reservation.status`는 `String` 타입으로, enum 검증이 적용되지 않음

---

## 6. 트랜잭션 전략

### 기본 설정

```java
@Service
@Transactional(readOnly = true)  // 클래스 레벨: 읽기 전용
public class SomeService {

    @Transactional  // 메서드 레벨: 쓰기 가능
    public void writeOperation() { ... }
}
```

| 레이어 | 기본 설정 | 쓰기 작업 |
|--------|----------|----------|
| Service | `readOnly = true` | 메서드별 `@Transactional` |

---

## 7. 인증/인가

### JWT 토큰

- 로그인 성공 시 JWT 토큰 발급
- 토큰에 `role: ADMIN` 클레임 포함
- `/admin/**` 경로는 유효한 JWT 필요

### 관리자 계정

- 설정 파일에서 username/password 로드
- 단일 관리자 계정만 지원
