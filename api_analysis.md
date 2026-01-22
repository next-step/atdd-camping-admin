# API 분석 (Admin)

## 1. 예약 상태 변경
**API Endpoint**: `PATCH /admin/reservations/{id}/status`

**목적**: 특정 예약의 상태(예: 예약 확정, 취소 등)를 변경합니다.

**파라미터**:

| 구분 | 이름 | 타입 | 필수 여부 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| Path | id | Long | Y | 예약 ID |
| Body | status | String | Y | 변경할 상태값 (예: "CANCELLED") |

**응답 (JSON)**:
```json
{
  "id": 1,
  "customerName": "홍길동",
  "startDate": "2024-01-01",
  "endDate": "2024-01-03",
  "status": "CANCELLED",
  "campsiteSiteNumber": "A1",
  "reservationDate": "2023-12-01"
}
```

**비즈니스 규칙 & 로직**:
*   **예약 조회**: Path Variable로 전달된 `id`로 예약을 조회합니다. 존재하지 않으면 `IllegalArgumentException` (500 Error)이 발생합니다.
*   **유효성 검사**: Body가 비어있으면 400 Bad Request를 반환합니다.
*   **상태 업데이트**: `status` 값이 비어있지 않거나 공백이 아닌 경우에만 해당 값으로 예약 상태를 덮어씁니다. 별도의 ENUM 검증 없이 문자열 그대로 저장됩니다(유연하지만 주의 필요).

---

## 2. 예약 목록 조회
**API Endpoint**: `GET /admin/reservations`

**목적**: 시스템에 등록된 모든 예약 내역을 조회합니다.

**파라미터**: 없음

**응답 (JSON)**:
```json
[
  {
    "id": 1,
    "customerName": "홍길동",
    "startDate": "2024-01-01",
    "endDate": "2024-01-03",
    "status": "CONFIRMED",
    "campsiteSiteNumber": "A1",
    "reservationDate": "2023-12-01"
  },
  ...
]
```

**비즈니스 규칙 & 로직**:
*   **전체 조회**: 페이징 없이 모든 예약 데이터를 조회합니다.
*   **Null 처리**: 결과 리스트 혹은 내부 요소가 null인 경우를 방어하여 안전한 리스트를 반환합니다.

---

## 3. 상품 생성
**API Endpoint**: `POST /admin/products`

**목적**: 매점 판매 물품 또는 대여 물품을 새로 등록합니다.

**파라미터**:

| 구분 | 이름 | 타입 | 필수 여부 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| Body | name | String | Y | 상품명 |
| Body | stockQuantity | Integer | N | 재고 수량 (기본값: 0) |
| Body | price | Number | N | 가격 (기본값: 0) |
| Body | productType | String | N | 상품 유형 ("SALE", "RENTAL") (기본값: "SALE") |

**응답 (JSON)**:
```json
{
  "id": 10,
  "name": "장작",
  "stockQuantity": 50,
  "price": 10000,
  "productType": "SALE"
}
```

**비즈니스 규칙 & 로직**:
*   **파싱 및 기본값**: Request Body가 `Map<String, Object>` 형태이며, 각 필드 파싱 실패 시 기본값(0, SALE 등)이 적용됩니다.
*   **유형 처리**: `productType`이 유효하지 않은 ENUM 문자열이면 `SALE`로 처리됩니다.

---

## 4. 캠프사이트 생성
**API Endpoint**: `POST /admin/campsites`

**목적**: 캠핑장 내 텐트를 칠 수 있는 구역(사이트) 정보를 생성합니다.

**파라미터**:

| 구분 | 이름 | 타입 | 필수 여부 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| Body | siteNumber | String | Y | 사이트 번호/이름 (예: "A1") |
| Body | description | String | N | 설명 |
| Body | maxPeople | Integer | N | 최대 수용 인원 |

**응답 (JSON)**:
```json
{
  "id": 5,
  "siteNumber": "A5",
  "description": "데크 사이트",
  "maxPeople": 4
}
```

**비즈니스 규칙 & 로직**:
*   **파싱**: 숫자 파싱 에러 시 `maxPeople`은 `null`로 저장됩니다.
*   **성공 응답**: 생성 성공 시 `201 Created` 상태 코드를 반환합니다.

---

## 5. 대여 생성
**API Endpoint**: `POST /admin/rentals`

**목적**: 고객에게 대여용 물품(예: 릴선, 화로대)을 대여해주고 기록을 생성합니다. 재고가 차감됩니다.

**파라미터**:

| 구분 | 이름 | 타입 | 필수 여부 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| Body | productId | Long | Y | 대여할 상품 ID |
| Body | quantity | Integer | Y | 대여 수량 |
| Body | reservationId | Long | N | 관련 예약 ID (없을 시 현장 대여 등) |

**응답 (JSON)**:
```json
{
  "id": 20,
  "reservationId": 1,
  "productId": 10,
  "productName": "릴선",
  "quantity": 1,
  "isReturned": false,
  "createdAt": "2024-01-01T14:00:00"
}
```

**비즈니스 규칙 & 로직**:
*   **상품 검증**: `productId`로 상품을 조회합니다. 없으면 예외 발생.
*   **타입 검증**: 해당 상품의 `productType`이 `RENTAL`이어야 합니다. 아니면 예외 발생.
*   **재고 차감**: 요청한 `quantity`만큼 재고를 즉시 차감합니다. 재고가 부족하면 `IllegalStateException` 예외가 발생하여 대여가 불가능합니다.
*   **예약 연동**: `reservationId`가 제공되면 해당 예약 정보를 연결하여 저장합니다.
*   **성공 응답**: 생성 성공 시 `201 Created` 상태 코드를 반환합니다.
