# 예약 상태 전이 규칙

## 허용되는 상태 전이

| 현재 상태 | 변경 가능 상태 |
|-----------|----------------|
| CONFIRMED | CANCELLED, CHECKED_IN |
| CHECKED_IN | CHECKED_OUT, CANCELLED |

## 거부되는 상태 전이

| 현재 상태 | 거부 사유 |
|-----------|-----------|
| CANCELLED | 모든 전이 거부 (최종 상태) |
| CHECKED_OUT | 모든 전이 거부 (최종 상태) |
| 동일 상태 | 같은 상태로의 전이 거부 |

## 상태 전이 다이어그램

```
CONFIRMED ──────┬──────> CANCELLED (최종)
                │
                └──────> CHECKED_IN ──────> CHECKED_OUT (최종)
```

## 구현 시 고려사항

- 유효하지 않은 상태 전이 요청 시 예외 발생
- 예외 타입: `IllegalStateException` 또는 커스텀 예외
- HTTP 응답: 400 Bad Request 또는 409 Conflict