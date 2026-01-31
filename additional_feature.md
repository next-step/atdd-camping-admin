# 1. 예약 변경 기능
## 예약 상태 전이 규칙

### 허용되는 상태 전이

| 현재 상태 | 변경 가능 상태 |
|-----------|----------------|
| CONFIRMED | CANCELLED, CHECKED_IN |
| CHECKED_IN | CHECKED_OUT, CANCELLED |

### 거부되는 상태 전이

| 현재 상태 | 거부 사유 |
|-----------|-----------|
| CANCELLED | 모든 전이 거부 (최종 상태) |
| CHECKED_OUT | 모든 전이 거부 (최종 상태) |
| 동일 상태 | 같은 상태로의 전이 거부 |

### 상태 전이 다이어그램

```
CONFIRMED ──────┬──────> CANCELLED (최종)
                │
                └──────> CHECKED_IN ──────> CHECKED_OUT (최종)
```

### 구현 시 고려사항

- 유효하지 않은 상태 전이 요청 시 예외 발생
- 예외 타입: `IllegalStateException` 또는 커스텀 예외
- HTTP 응답: 400 Bad Request 또는 409 Conflict

---

# 2. 상품 등록 기능

## 입력값 검증 규칙

### 필수 필드 검증

| 필드 | 검증 | 실패 시 |
|------|------|---------|
| name | `@NotBlank` | 400 Bad Request |

### 값 범위 검증

| 필드 | 검증 | 허용 값 | 실패 시 |
|------|------|---------|---------|
| stockQuantity | `@PositiveOrZero` | 0 이상 | 400 Bad Request |
| price | `@PositiveOrZero` | 0 이상 | 400 Bad Request |

### 기본값 처리

| 필드 | 기본값 | 조건 |
|------|--------|------|
| stockQuantity | 0 | 값이 null일 때 |
| price | 0 | 값이 null일 때 |
| productType | SALE | 값이 null일 때 |

### 검증 흐름

```
요청 → @Valid 검증 → 기본값 적용 → Entity 생성 → 저장
         ↓ 실패
    400 Bad Request
```

---

# 3. 캠핑장 사이트 등록 기능

## 추가된 검증 로직

### 사이트 번호 검증

| 검증 항목     | 조건            | 실패 시 |
|-----------|---------------|---------|
| 사이트 번호 필수 | null 또는 빈 문자열 | 400 Bad Request |
| 사이트 번호 중복 | 기존에 이미 존재     | 409 Conflict |

### 최대 인원 제한 및 기본값 처리

| 검증 항목     | 조건 | 실패 시 |
|-----------|------|---------|
| 최대 인원 최소값 | 1 미만 | 400 Bad Request |
| 최대 인원 최대값 | 30 초과 | 400 Bad Request |
| 최대 인원 기본값 | 1 | 값이 null일 때 |


## 검증 흐름

```
요청 → siteNumber 검증 → 중복 체크 → maxPeople 검증 → Entity 생성 → 저장
         ↓ null/empty      ↓ 중복        ↓ 범위 벗어남
      400 Bad Request   409 Conflict   400 Bad Request
```
