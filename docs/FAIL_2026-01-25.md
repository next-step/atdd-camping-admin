# 인수테스트 중 발견된 문제점

## 1. 예약 상태 변경 API - Request Body 파싱 실패
- 증상: PATCH `/admin/reservations/{id}/status` 요청 시 400 에러
- 원인: `@RequestBody Map<String, Object>` 타입이 제대로 파싱되지 않음
- 해결: `StatusUpdateRequest` DTO 클래스 생성 및 적용

## 2. 상품 생성 API - Request Body 파싱 실패
- 증상: POST `/admin/products` 요청 시 500 에러
- 원인: `@RequestBody Map<String, Object>` 타입이 null로 들어옴
- 분석:
  - Spring Boot에서 `Map<String, Object>`는 일부 상황에서 파싱 실패
  - 동일한 코드가 다른 컨트롤러(Campsite)에서는 동작하는 경우도 있어 원인 불명확
  - DTO를 사용하면 타입 안전성 + Jackson 역직렬화 안정성 확보
- 해결: `CreateProductRequest` DTO 클래스 생성 및 적용
- 권장: `Map<String, Object>` 대신 항상 DTO 사용

## 3. Primary Key 충돌
- 증상: 새 레코드 생성 시 "Primary Key violation" 에러
- 원인: H2 데이터베이스에서 `data.sql`로 명시적 ID 삽입 후 auto-increment 시퀀스가 리셋되지 않음
- 분석:
  ```sql
  -- data.sql에서 ID 1, 2, 3으로 삽입
  INSERT INTO products (id, name, ...) VALUES (1, '랜턴', ...);

  -- 이후 새 상품 생성 시 ID가 1부터 시작하여 충돌
  INSERT INTO products (name, ...) VALUES ('새상품', ...); -- ID=1 시도 → 충돌!
  ```
- 해결: **높은 ID 대역 사용** (1001~)
  ```sql
  -- 초기 데이터는 1001번대 ID 사용
  INSERT INTO campsites (id, site_number, ...) VALUES (1001, 'A-01', ...);
  INSERT INTO reservations (id, campsite_id, ...) VALUES (1001, 1001, ...);

  -- 테스트에서 새로 생성되는 데이터는 auto-increment로 1, 2, 3... → 충돌 없음
  ```
- 장점: ALTER TABLE RESTART 없이 깔끔하게 해결, FK 참조도 명시적으로 유지

## 4. 순환 참조로 인한 StackOverflowError
- 증상: 캠핑장 수정 API 호출 시 StackOverflowError
- 원인: Campsite ↔ Reservation 양방향 JPA 관계
- 분석:
  ```
  Campsite.reservations → Reservation.campsite → Campsite.reservations → ...
  Jackson이 JSON 직렬화 시 무한 루프 발생
  ```
- 해결: **불필요한 양방향 매핑 제거** 
  - `campsite.getReservations()` 사용처 없음 확인
  - `Campsite.reservations` 필드 삭제 → 단방향 관계로 변경
  - 필요시 `reservationRepository.findByCampsiteId()` 사용
- 권장: 양방향 매핑은 실제로 양쪽 탐색이 필요한 경우에만 사용

## 5. 예외 처리 부재
- 증상: 비즈니스 예외 발생 시 500 에러 반환
- 원인: 전역 예외 핸들러 없음
- 해결: `GlobalExceptionHandler` 추가
  - `IllegalStateException` → 409 Conflict
  - `IllegalArgumentException` → 400 Bad Request
  - `DataIntegrityViolationException` → 409 Conflict

