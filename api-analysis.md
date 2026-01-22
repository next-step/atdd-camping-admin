# 캠핑 관리자 프로젝트 - API 분석

## 프로젝트 구조

```
src/main/java/com/camping/admin/
├── controller/              # REST API 엔드포인트
│   ├── AuthController
│   ├── ReservationAdminController
│   ├── RevenueAdminController
│   ├── ProductAdminController
│   ├── RentalAdminController
│   ├── CampsiteAdminController
│   └── SalesController
├── service/                 # 비즈니스 로직
├── domain/
│   ├── entity/              # JPA 엔티티
│   └── enums/               # 상태 열거형
├── dto/                     # 요청/응답 DTO
├── repository/              # 데이터 접근 계층
└── security/                # JWT 인증
```

---

## API 엔드포인트 요약

| HTTP | 엔드포인트 | 설명 | 인증 |
|------|-----------|------|:----:|
| POST | `/auth/login` | 로그인 | - |
| GET | `/admin/reservations` | 예약 목록 조회 | O |
| PATCH | `/admin/reservations/{id}/status` | 예약 상태 변경 | O |
| GET | `/admin/products` | 상품 목록 조회 | O |
| POST | `/admin/products` | 상품 생성 | O |
| PUT | `/admin/products/{id}` | 상품 수정 | O |
| GET | `/admin/campsites` | 캠프장 목록 조회 | O |
| POST | `/admin/campsites` | 캠프장 생성 | O |
| PUT | `/admin/campsites/{id}` | 캠프장 수정 | O |
| GET | `/admin/rentals` | 대여 목록 조회 | O |
| POST | `/admin/rentals` | 대여 생성 | O |
| PATCH | `/admin/rentals/{id}/return` | 대여 반납 | O |
| GET | `/admin/reports/revenue/daily` | 일일 수익 보고서 | O |
| GET | `/admin/reports/revenue/range` | 기간 수익 보고서 | O |
| GET | `/admin/reports/revenue/range/entries` | 수익 상세 항목 | O |
| POST | `/api/sales` | 판매 처리 | - |
| GET | `/api/sales` | 판매 목록 조회 | - |

---

## 1. 인증 API

### POST /auth/login
관리자 로그인 후 JWT 토큰 발급

**Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200):**
```json
{
  "accessToken": "jwt-token-string"
}
```

**Response (401):** `"Invalid credentials"`

---

## 2. 예약 관리 API

### GET /admin/reservations
전체 예약 목록 조회

**Response (200):**
```json
[
  {
    "id": 1,
    "customerName": "김철수",
    "startDate": "2024-01-15",
    "endDate": "2024-01-17",
    "status": "CONFIRMED",
    "campsiteSiteNumber": "A-001",
    "reservationDate": "2024-01-10"
  }
]
```

### PATCH /admin/reservations/{reservationId}/status
예약 상태 변경

**Request:**
```json
{
  "status": "CONFIRMED"
}
```

**ReservationStatus 종류:**
- `WAITING` - 대기중
- `PENDING` - 처리중
- `CONFIRMED` - 확정
- `REJECTED` - 거절
- `CHECKED_IN` - 체크인
- `CHECKED_OUT` - 체크아웃
- `CANCELLED` - 취소

---

## 3. 상품 관리 API

### GET /admin/products
전체 상품 목록 조회

**Response (200):**
```json
[
  {
    "id": 1,
    "name": "텐트",
    "stockQuantity": 50,
    "price": 150000.00,
    "productType": "RENTAL"
  }
]
```

### POST /admin/products
상품 생성

**Request:**
```json
{
  "name": "텐트",
  "stockQuantity": 50,
  "price": 150000,
  "productType": "RENTAL"
}
```

**ProductType 종류:**
- `SALE` - 판매용
- `RENTAL` - 대여용

### PUT /admin/products/{productId}
상품 수정 (부분 업데이트 가능)

---

## 4. 캠프장 관리 API

### GET /admin/campsites
전체 캠프장 사이트 목록

**Response (200):**
```json
[
  {
    "id": 1,
    "siteNumber": "A-001",
    "description": "산림뷰가 멋진 사이트",
    "maxPeople": 4
  }
]
```

### POST /admin/campsites
캠프장 사이트 생성

**Request:**
```json
{
  "siteNumber": "A-001",
  "description": "산림뷰가 멋진 사이트",
  "maxPeople": 4
}
```

### PUT /admin/campsites/{campsiteId}
캠프장 수정

---

## 5. 대여 관리 API

### GET /admin/rentals
전체 대여 기록 조회

**Response (200):**
```json
[
  {
    "id": 1,
    "reservationId": 5,
    "productId": 1,
    "productName": "텐트",
    "quantity": 1,
    "isReturned": false,
    "createdAt": "2024-01-11T14:20:00"
  }
]
```

### POST /admin/rentals
대여 생성

**Request:**
```json
{
  "reservationId": 5,
  "productId": 1,
  "quantity": 1
}
```

> `reservationId`는 선택사항 (Walk-in 대여 지원)

### PATCH /admin/rentals/{rentalRecordId}/return
대여 반납 처리 (재고 자동 복구)

---

## 6. 수익 보고서 API

### GET /admin/reports/revenue/daily?date=2024-01-15
일일 수익 보고서

**Response (200):**
```json
{
  "date": "2024-01-15",
  "totalReservationRevenue": 100000,
  "totalSalesRevenue": 50000,
  "totalRentalRevenue": 25000,
  "grandTotalRevenue": 175000
}
```

### GET /admin/reports/revenue/range?from=2024-01-01&to=2024-01-31
기간 수익 보고서

### GET /admin/reports/revenue/range/entries?from=2024-01-01&to=2024-01-31
기간별 수익 상세 항목

**Response (200):**
```json
[
  {
    "type": "RESERVATION",
    "title": "예약 #5",
    "amount": 100000,
    "occurredAt": "2024-01-10T10:30:00"
  }
]
```

---

## 7. 판매 API (공개)

### POST /api/sales
상품 판매 처리

**Request:**
```json
{
  "items": [
    { "productId": 2, "quantity": 5 },
    { "productId": 3, "quantity": 2 }
  ]
}
```

### GET /api/sales
최근 판매 기록 10개 조회

---

## 도메인 모델 관계

```
Campsite (캠프장)
    │
    │ 1:N
    ▼
Reservation (예약) ──────┬──── N:1 ──── Customer
    │                    │
    │ 1:N                │
    ▼                    │
RentalRecord (대여)      │
    │                    │
    │ N:1                │
    ▼                    │
Product (상품) ◄─────────┘
    │
    │ 1:N
    ▼
SalesRecord (판매)
```

---

## 비즈니스 로직

### 재고 관리
- **판매/대여 시**: 재고 감소
- **반납 시**: 재고 복구
- **재고 부족 시**: 예외 발생

### 수익 계산
```
예약 수익 = (종료일 - 시작일) × 50,000원 (최소 1박)
판매 수익 = 상품 가격 × 수량
대여 수익 = 상품 가격 × 수량
```

---

## 인증 방식

**JWT 토큰 전달 (우선순위):**
1. `Authorization: Bearer <token>` 헤더
2. `AUTH_TOKEN` 쿠키

**인증 제외 경로:**
- `/auth/login`, `/login`
- `/api/sales`
- 정적 리소스 (`/css/**`, `/js/**`, `/images/**`)
- `/h2-console/**`