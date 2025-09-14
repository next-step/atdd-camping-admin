# 🌐 API 레퍼런스 가이드

## 📋 개요

초록 캠핑장 관리자 시스템의 모든 REST API 엔드포인트를 정리한 문서입니다.

### 🔗 Base URL
- **개발환경**: http://localhost:8081

### 🔐 인증 방식
- **JWT 토큰**: `Authorization: Bearer {token}` 헤더 또는 `AUTH_TOKEN` 쿠키 사용
- **관리자 계정**: `admin` / `admin123`

---

## 🔑 인증 API

### POST /auth/login
관리자 로그인

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (401 Unauthorized):**
```json
"Invalid credentials"
```

---

## 🏕️ 캠핑장 관리 API

### GET /admin/campsites
전체 캠핑장 목록 조회

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "siteNumber": "A-1",
    "description": "숲속 조용한 자리",
    "maxPeople": 4,
    "status": "AVAILABLE"
  }
]
```

### POST /admin/campsites
새 캠핑장 추가

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Request Body:**
```json
{
  "siteNumber": "A-1",
  "description": "숲속 조용한 자리",
  "maxPeople": 4
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "siteNumber": "A-1",
  "description": "숲속 조용한 자리",
  "maxPeople": 4,
  "status": "AVAILABLE"
}
```

### PUT /admin/campsites/{campsiteId}
캠핑장 정보 수정

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Path Parameters:**
- `campsiteId`: 캠핑장 ID (Long)

**Request Body:**
```json
{
  "siteNumber": "A-2",
  "description": "호수 근처 전망 좋은 자리",
  "maxPeople": 6
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "siteNumber": "A-2",
  "description": "호수 근처 전망 좋은 자리",
  "maxPeople": 6,
  "status": "AVAILABLE"
}
```

---

## 📦 상품 관리 API

### GET /admin/products
전체 상품 목록 조회

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "텐트",
    "stockQuantity": 10,
    "price": 50000,
    "productType": "RENTAL"
  }
]
```

### POST /admin/products
새 상품 추가

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Request Body:**
```json
{
  "name": "텐트",
  "stockQuantity": 10,
  "price": 50000,
  "productType": "RENTAL"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "텐트",
  "stockQuantity": 10,
  "price": 50000,
  "productType": "RENTAL"
}
```

### PUT /admin/products/{productId}
상품 정보 수정

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Path Parameters:**
- `productId`: 상품 ID (Long)

**Request Body:**
```json
{
  "name": "대형 텐트",
  "stockQuantity": 8,
  "price": 70000,
  "productType": "RENTAL"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "대형 텐트",
  "stockQuantity": 8,
  "price": 70000,
  "productType": "RENTAL"
}
```

---

## 📋 예약 관리 API

### GET /admin/reservations
전체 예약 목록 조회

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "customerName": "김철수",
    "campsiteId": 1,
    "checkInDate": "2023-12-01",
    "checkOutDate": "2023-12-03",
    "status": "CONFIRMED"
  }
]
```

### PATCH /admin/reservations/{reservationId}/status
예약 상태 변경

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Path Parameters:**
- `reservationId`: 예약 ID (Long)

**Request Body:**
```json
{
  "status": "CANCELLED"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "customerName": "김철수",
  "campsiteId": 1,
  "checkInDate": "2023-12-01",
  "checkOutDate": "2023-12-03",
  "status": "CANCELLED"
}
```

---

## 🔄 대여 관리 API

### GET /admin/rentals
전체 대여 기록 조회

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "productId": 1,
    "quantity": 2,
    "reservationId": 1,
    "rentalDate": "2023-12-01T10:00:00",
    "returnDate": null,
    "status": "RENTED"
  }
]
```

### POST /admin/rentals
새 대여 기록 생성

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2,
  "reservationId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "productId": 1,
  "quantity": 2,
  "reservationId": 1,
  "rentalDate": "2023-12-01T10:00:00",
  "returnDate": null,
  "status": "RENTED"
}
```

### PATCH /admin/rentals/{rentalRecordId}/return
대여 상품 반납 처리

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Path Parameters:**
- `rentalRecordId`: 대여 기록 ID (Long)

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": 1,
  "quantity": 2,
  "reservationId": 1,
  "rentalDate": "2023-12-01T10:00:00",
  "returnDate": "2023-12-03T14:00:00",
  "status": "RETURNED"
}
```

---

## 💰 매출/판매 API

### POST /api/sales
판매 처리 (Public API)

**Request Body:**
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 25000
    }
  ],
  "customerId": 1
}
```

**Response (200 OK):**
```json
{}
```

### GET /api/sales
최근 판매 기록 조회 (Public API)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "customerId": 1,
    "saleDate": "2023-12-01T15:30:00",
    "totalAmount": 50000,
    "items": [
      {
        "productId": 1,
        "productName": "생수",
        "quantity": 2,
        "unitPrice": 25000
      }
    ]
  }
]
```

---

## 📊 매출 리포트 API

### GET /admin/reports/revenue/daily
일일 매출 리포트

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Query Parameters:**
- `date`: 조회할 날짜 (YYYY-MM-DD 형식)

**Example:**
```
GET /admin/reports/revenue/daily?date=2023-12-01
```

**Response (200 OK):**
```json
{
  "date": "2023-12-01",
  "totalRevenue": 150000,
  "rentalRevenue": 100000,
  "salesRevenue": 50000,
  "transactionCount": 5
}
```

### GET /admin/reports/revenue/range
기간별 매출 리포트

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Query Parameters:**
- `from`: 시작 날짜 (YYYY-MM-DD 형식)
- `to`: 종료 날짜 (YYYY-MM-DD 형식)

**Example:**
```
GET /admin/reports/revenue/range?from=2023-12-01&to=2023-12-07
```

**Response (200 OK):**
```json
{
  "from": "2023-12-01",
  "to": "2023-12-07",
  "totalRevenue": 1050000,
  "averageDailyRevenue": 150000,
  "peakDay": {
    "date": "2023-12-03",
    "revenue": 200000
  }
}
```

### GET /admin/reports/revenue/range/entries
기간별 매출 세부 내역

**Headers:**
- `Authorization: Bearer {token}` (필수)

**Query Parameters:**
- `from`: 시작 날짜 (YYYY-MM-DD 형식)
- `to`: 종료 날짜 (YYYY-MM-DD 형식)

**Example:**
```
GET /admin/reports/revenue/range/entries?from=2023-12-01&to=2023-12-07
```

**Response (200 OK):**
```json
[
  {
    "date": "2023-12-01",
    "revenue": 150000,
    "transactionCount": 5
  },
  {
    "date": "2023-12-02",
    "revenue": 120000,
    "transactionCount": 3
  }
]
```

---

## 🏷️ 데이터 타입 정의

### ProductType (Enum)
- `RENTAL`: 대여 상품
- `SALE`: 판매 상품

### CampsiteStatus (Enum)
- `AVAILABLE`: 이용 가능
- `OCCUPIED`: 사용 중
- `MAINTENANCE`: 정비 중

### ReservationStatus (Enum)
- `PENDING`: 대기 중
- `CONFIRMED`: 확정
- `CANCELLED`: 취소
- `COMPLETED`: 완료

---

## 🚨 에러 응답

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Invalid request parameters"
}
```

### 401 Unauthorized
```json
"Invalid credentials"
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Cannot find resource with id: 123"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## 📝 문서 업데이트 가이드

이 API 문서는 다음 상황에서 업데이트해야 합니다:

### 🔄 자동 업데이트 트리거
- **새로운 Controller 또는 엔드포인트 추가**
- **기존 API의 요청/응답 스키마 변경**  
- **새로운 DTO 클래스 추가**
- **Enum 타입 추가/수정**

### 📋 업데이트 체크리스트
- [ ] 새로운 엔드포인트의 URL, HTTP 메서드, 인증 요구사항 확인
- [ ] 요청/응답 예제가 실제 DTO 클래스와 일치하는지 검증
- [ ] 에러 응답 코드가 Controller에서 실제 반환하는 값과 일치하는지 확인
- [ ] Enum 값들이 최신 상태인지 검증

---

**📚 관련 문서**: [CLAUDE.md](../CLAUDE.md) | [아키텍처 가이드](./architecture.md) | [테스트 가이드](./testing-guide.md)