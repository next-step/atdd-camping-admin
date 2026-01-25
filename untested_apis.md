# 테스트가 없는 API 목록

이 문서는 프로젝트 내에서 자동화된 테스트(Cucumber 인수 테스트 등)가 구현되지 않은 API 엔드포인트를 정리한 목록입니다.

## 미테스트 API 목록

### 1. 캠프사이트 관리 (CampsiteAdminController)
- [x] `PUT /admin/campsites/{campsiteId}`: 캠프사이트 정보 수정 (테스트 완료)

### 2. 상품 관리 (ProductAdminController)
- [x] `PUT /admin/products/{productId}`: 상품 정보 수정 (리팩토링 필요하나 테스트 시나리오는 추가 가능)
- [x] `GET /admin/products?q={query}`: 상품 이름 검색 (신규 추가 및 테스트 완료)

### 3. 대여 관리 (RentalAdminController)
- `GET /admin/rentals`: 대여 기록 목록 조회
- `PATCH /admin/rentals/{rentalRecordId}/return`: 물품 반납 처리

### 4. 매출 보고서 (RevenueAdminController)
- `GET /admin/reports/revenue/daily`: 일별 매출 보고서 조회
- `GET /admin/reports/revenue/range`: 기간별 매출 보고서 요약 조회
- `GET /admin/reports/revenue/range/entries`: 기간별 상세 매출 내역 조회

### 5. 판매 관리 (SalesController)
- `POST /api/sales`: 판매 처리
- `GET /api/sales`: 판매 기록 목록 조회

### 6. 인증 (AuthController)
- `POST /auth/login`: 로그인 및 JWT 토큰 발급

---
*참고: 테스트 여부는 `src/test/resources/features`에 정의된 인수 테스트를 기준으로 판단되었습니다.*
