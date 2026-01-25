# API 엔드포인트 문서

> 캠핑장 관리 시스템 API 레퍼런스

## 개요

| 구분 | 경로 패턴 | 인증 필요 |
|------|----------|----------|
| 관리자 API | `/admin/**` | O (JWT) |
| 공개 API | `/api/**` | X |

**총 20개 엔드포인트**

---

## 인증 (AuthController)

### POST `/auth/login`
관리자 로그인 및 JWT 토큰 발급

**Request**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
- 토큰은 응답 본문과 쿠키에 함께 설정됨

---

## 예약 관리 (ReservationAdminController)

### GET `/admin/reservations`
전체 예약 목록 조회

**Response**
```json
[
  {
    "id": 1,
    "guestName": "홍길동",
    "startDate": "2024-01-15",
    "endDate": "2024-01-17",
    "status": "CONFIRMED",
    "campsiteSiteNumber": "A-1"
  }
]
```

### PATCH `/admin/reservations/{id}/status`
예약 상태 변경

**Request**
```json
{
  "status": "CONFIRMED"
}
```

**유효한 상태값**
- `PENDING` - 대기중
- `CONFIRMED` - 확정
- `CANCELLED` - 취소
- `COMPLETED` - 완료

---

## 캠프사이트 관리 (CampsiteAdminController)

### GET `/admin/campsites`
전체 캠프사이트 조회

**Response**
```json
[
  {
    "id": 1,
    "siteNumber": "A-1",
    "description": "숲속 뷰 사이트",
    "maxPeople": 4
  }
]
```

### POST `/admin/campsites`
캠프사이트 생성

**Request**
```json
{
  "siteNumber": "A-2",
  "description": "호수 뷰 사이트",
  "maxPeople": 6
}
```

### PUT `/admin/campsites/{id}`
캠프사이트 수정

**Request**
```json
{
  "siteNumber": "A-2",
  "description": "호수 뷰 프리미엄 사이트",
  "maxPeople": 8
}
```

---

## 상품 관리 (ProductAdminController)

### GET `/admin/products`
전체 상품 조회

**Response**
```json
[
  {
    "id": 1,
    "name": "텐트",
    "stockQuantity": 10,
    "price": 30000,
    "productType": "RENTAL"
  }
]
```

### POST `/admin/products`
상품 생성

**Request**
```json
{
  "name": "캠핑 의자",
  "stockQuantity": 20,
  "price": 5000,
  "productType": "SALE"
}
```

**상품 타입**
- `RENTAL` - 대여 상품
- `SALE` - 판매 상품

### PUT `/admin/products/{id}`
상품 수정

---

## 대여 관리 (RentalAdminController)

### GET `/admin/rentals`
전체 대여 기록 조회

**Response**
```json
[
  {
    "id": 1,
    "productId": 1,
    "productName": "텐트",
    "quantity": 2,
    "reservationId": 1,
    "isReturned": false,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

### POST `/admin/rentals`
대여 생성

**Request**
```json
{
  "productId": 1,
  "quantity": 2,
  "reservationId": 1
}
```
- `reservationId`는 선택사항 (워크인 대여 지원)

### PATCH `/admin/rentals/{id}/return`
반납 처리

**Response**
- 반납 완료된 대여 기록 반환
- `isReturned: true`로 변경됨

---

## 판매 (SalesController)

### GET `/api/sales`
최근 10건 판매 기록 조회

**Response**
```json
[
  {
    "id": 1,
    "items": [
      { "productId": 2, "productName": "장작", "quantity": 3, "price": 15000 }
    ],
    "totalPrice": 15000,
    "createdAt": "2024-01-15T14:20:00"
  }
]
```

### POST `/api/sales`
판매 처리

**Request**
```json
{
  "items": [
    { "productId": 2, "quantity": 3 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

---

## 매출 리포트 (RevenueAdminController)

### GET `/admin/reports/revenue/daily`
일별 매출 리포트

**Query Params**
- `date`: 조회 날짜 (YYYY-MM-DD)

**Response**
```json
{
  "date": "2024-01-15",
  "salesRevenue": 150000,
  "reservationRevenue": 100000,
  "rentalRevenue": 60000,
  "totalRevenue": 310000
}
```

### GET `/admin/reports/revenue/range`
기간별 매출 합계

**Query Params**
- `from`: 시작일 (YYYY-MM-DD)
- `to`: 종료일 (YYYY-MM-DD)

**Response**
```json
{
  "from": "2024-01-01",
  "to": "2024-01-31",
  "totalSalesRevenue": 1500000,
  "totalReservationRevenue": 2000000,
  "totalRentalRevenue": 600000,
  "grandTotal": 4100000
}
```

### GET `/admin/reports/revenue/range/entries`
기간별 상세 항목 목록

**Query Params**
- `from`: 시작일 (YYYY-MM-DD)
- `to`: 종료일 (YYYY-MM-DD)

**Response**
```json
[
  {
    "type": "SALE",
    "description": "장작 x 3",
    "amount": 15000,
    "date": "2024-01-15"
  },
  {
    "type": "RESERVATION",
    "description": "A-1 사이트 (2박)",
    "amount": 100000,
    "date": "2024-01-15"
  }
]
```
