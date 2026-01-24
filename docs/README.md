# API 엔드포인트

| 도메인 | 엔드포인트 | 메서드 |
|--------|-----------|--------|
| 인증 | `/auth/login` | POST |
| 예약 | `/admin/reservations` | GET |
|  | `/admin/reservations/{id}/status` | PATCH |
| 캠핑장 | `/admin/campsites` | GET, POST |
|  | `/admin/campsites/{id}` | PUT |
| 상품 | `/admin/products` | GET, POST |
|  | `/admin/products/{id}` | PUT |
| 대여 | `/admin/rentals` | GET, POST |
|  | `/admin/rentals/{id}/return` | PATCH |
| 판매 | `/api/sales` | GET, POST |
| 매출 | `/admin/reports/revenue/daily` | GET |
|  | `/admin/reports/revenue/range` | GET |