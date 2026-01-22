# 기능 분석 및 리스크 판단

## 분석 대상 엔드포인트

- ReservationAdminController: [PATCH] /admin/reservations/{id}/status, [GET] /admin/reservations
- ProductAdminController: [POST] /admin/products
- CampsiteAdminController: [POST] /admin/campsites
- RentalAdminController: [POST] /admin/rentals

---

## 1. ReservationAdminController

### [GET] /admin/reservations

**기능**: 모든 예약 목록 조회

**구현 분석** (`ReservationAdminController.java:24-42`):
- Repository에서 직접 조회 후 `ReservationResponse`로 변환
- null 체크 로직이 과도하게 복잡함 (JPA findAll()은 null을 반환하지 않음)

**리스크**: 낮음
- 단순 조회 기능으로 데이터 변경 없음
- 인수테스트 시나리오: 예약이 있을 때/없을 때 목록 조회

---

### [PATCH] /admin/reservations/{id}/status

**기능**: 예약 상태 변경

**구현 분석** (`ReservationAdminController.java:44-70`):
- `Map<String, Object>`로 body를 받음 (타입 안전성 부족)
- **status 값 검증 없음** - 어떤 문자열이든 저장됨
- 빈 문자열/null일 경우 기존 값 유지

**리스크**: 중간

| 리스크 항목 | 설명 |
|------------|------|
| 유효하지 않은 상태값 | "INVALID_STATUS" 같은 값도 저장 가능 |
| 상태 전이 규칙 부재 | CANCELLED → CONFIRMED 같은 비정상 전이 허용 |
| 비즈니스 로직 부재 | 취소 시 환불, 알림 등 후속 처리 없음 |

**인수테스트 필요 시나리오**:
- 정상적인 상태 변경 (CONFIRMED → CANCELLED)
- 존재하지 않는 예약 ID로 요청
- 빈 body 또는 status 없이 요청

---

## 2. ProductAdminController

### [POST] /admin/products

**기능**: 상품 등록

**구현 분석** (`ProductAdminController.java:36-104`):
- `Map<String, Object>`로 body를 받음
- 각 필드를 수동으로 파싱
- 파싱 실패 시 기본값으로 대체 (ZERO, ProductType.SALE 등)

**리스크**: 중간

| 리스크 항목 | 설명 |
|------------|------|
| name null 허용 | DB `nullable = false`인데 null로 생성 시도 가능 → 예외 발생 |
| 음수 재고/가격 | stockQuantity=-100, price=-5000 허용 |
| 기본값 숨김 처리 | 잘못된 입력이 조용히 기본값으로 처리됨 |

**인수테스트 필요 시나리오**:
- 정상적인 상품 등록
- 필수 필드(name) 누락
- 음수 재고/가격으로 등록 시도
- 잘못된 productType 값

---

## 3. CampsiteAdminController

### [POST] /admin/campsites

**기능**: 캠핑장 사이트 등록

**구현 분석** (`CampsiteAdminController.java:40-87`):
- `Map<String, Object>`로 body를 받음
- siteNumber가 null일 수 있음 (DB는 `nullable = false, unique = true`)

**리스크**: 중간

| 리스크 항목 | 설명 |
|------------|------|
| siteNumber null/중복 | unique 제약조건 위반 시 예외 발생 |
| maxPeople 검증 없음 | 음수나 0 허용 |
| description 기본값 | null → 빈 문자열로 처리 (의도적일 수 있음) |

**인수테스트 필요 시나리오**:
- 정상적인 캠핑장 사이트 등록
- siteNumber 중복 등록 시도
- siteNumber 누락
- 음수 maxPeople

---

## 4. RentalAdminController

### [POST] /admin/rentals

**기능**: 렌탈 기록 생성

**구현 분석** (`RentalAdminController.java:25-33`, `RentalService.java:33-54`):
- **잘 설계된 구조**: DTO(`CreateRentalRequest`) 사용, Service 레이어 분리
- ProductType 검증 있음 (RENTAL만 허용)
- 재고 차감 로직 포함 (`productService.decreaseStock`)

**리스크**: 낮음~중간

| 리스크 항목 | 설명 |
|------------|------|
| 재고 부족 처리 | decreaseStock 내부 구현 확인 필요 |
| quantity 검증 | 0 또는 음수 수량 허용 여부 |
| reservationId null | walk-in rental 허용 (의도적 설계) |

**인수테스트 필요 시나리오**:
- 정상적인 렌탈 등록 (예약 있음/없음)
- RENTAL 타입이 아닌 상품으로 렌탈 시도
- 재고 부족 상황
- 존재하지 않는 productId/reservationId

---

## 종합 리스크 매트릭스

| 엔드포인트 | 리스크 | 주요 이슈 |
|-----------|--------|----------|
| GET /admin/reservations | 낮음 | 단순 조회 |
| PATCH /admin/reservations/{id}/status | 중간 | 상태값 검증 없음 |
| POST /admin/products | 중간 | name null, 음수 값 허용 |
| POST /admin/campsites | 중간 | siteNumber 검증 부족 |
| POST /admin/rentals | 낮음 | 잘 설계됨, 재고 검증 확인 필요 |

---

## 인수테스트 작성 시 중점 사항

1. **정상 케이스**: 각 기능의 happy path 검증
2. **경계값 테스트**: 음수, 0, null, 빈 문자열
3. **예외 상황**: 존재하지 않는 ID, 중복 데이터, 제약조건 위반
4. **비즈니스 규칙**: 상태 전이, 타입 검증, 재고 관리