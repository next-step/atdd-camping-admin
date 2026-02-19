# Risk Analysis — ATDD 보호 대상 기능 선택

## 후보 기능 리스크 평가

| 기능 | 엔드포인트 | 리스크 | 요약 |
|---|---|---|---|
| 예약 상태 변경 | `PATCH /admin/reservations/{id}/status` | **높음** | 상태 검증 없음, enum 미사용, 전이 규칙 부재 |
| 예약 목록 조회 | `GET /admin/reservations` | 낮음 | 불필요한 null 체크만 존재, 동작 정상 |
| 상품 생성 | `POST /admin/products` | 중간 | verbose한 수동 파싱, 동작은 정상 |
| 캠프사이트 생성 | `POST /admin/campsites` | 중간 | 생성은 정상, update에 `.save()` 누락 버그 존재 |
| 대여 생성 | `POST /admin/rentals` | 중간 | 서비스 계층 활용, 비교적 깔끔 |

## 선택 기능: 예약 상태 변경

**엔드포인트:** `PATCH /admin/reservations/{id}/status`
**요청 본문:** `{ "status": "CONFIRMED" }`
**컨트롤러:** `ReservationAdminController.updateReservationStatus()`

## 발견된 문제점

### 1. 상태 값 검증 없음 (심각도: 높음)

현재 코드는 요청으로 들어온 문자열을 그대로 `reservation.setStatus()`에 대입한다.

```java
String statusValue = statusObj.toString();
reservation.setStatus(statusValue);
```

`ReservationStatus` enum이 존재하지만 전혀 사용하지 않아, `"HELLO_WORLD"` 같은 임의 문자열도 저장된다.

**유효한 상태 값:** `WAITING`, `PENDING`, `CONFIRMED`, `REJECTED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED`

### 2. 상태 전이 규칙 부재 (심각도: 높음)

어떤 상태에서든 어떤 상태로든 변경이 가능하다.

- `CANCELLED` → `CONFIRMED` (취소된 예약이 다시 확정)
- `CHECKED_OUT` → `WAITING` (퇴실 완료 후 대기 상태로 복귀)
- `CONFIRMED` → `CHECKED_OUT` (체크인 없이 바로 퇴실)

### 3. 엔티티 타입 불일치 (심각도: 중간)

`Reservation.status` 필드가 `String` 타입으로 선언되어 있어 컴파일 타임에 잘못된 값을 잡아낼 수 없다.

```java
// Reservation.java
private String status;  // ReservationStatus enum을 사용해야 함
```

### 4. 빈 문자열·null 처리의 무의미한 분기 (심각도: 낮음)

status가 null이면 아무 동작 없이 기존 값을 그대로 저장하고, 빈 문자열이면 기존 값을 유지한다. 에러 응답을 반환하지 않아 클라이언트가 요청 실패를 인지할 수 없다.

## 비즈니스 영향도

예약 상태는 캠핑장 운영의 핵심 데이터이다.

- **잘못된 상태 저장** → 예약 현황 대시보드 오염, 운영 혼란
- **비정상 전이 허용** → 취소된 예약의 체크인, 미확정 예약의 퇴실 등 운영 무결성 파괴
- **매출/통계 왜곡** → 상태 기반 리포트에 잘못된 데이터 포함

## 결론

**`PATCH /admin/reservations/{id}/status`를 첫 번째 ATDD 보호 대상으로 확정한다.**

가장 높은 비즈니스 리스크를 가지고 있으며, 상태 검증과 전이 규칙이라는 명확한 인수 조건을 테스트로 표현할 수 있다.
