# 리팩터링 기록

---

## 1. 도메인 로직 이동

서비스에 분산된 비즈니스 로직을 도메인 객체로 이동하여 캡슐화.

### 1.1 매출 계산 로직

| 대상 | 문제점 | 해결 | 효과 |
|------|--------|------|------|
| `Reservation` | 숙박일 계산 로직이 `RevenueService`에 3번 중복 | `calculateRevenue()` 메서드를 엔티티에 추가 | 중복 제거, 메서드 레퍼런스 활용 가능 |
| `RentalRecord` | 대여 매출 계산 시 엔티티 내부를 외부에서 조합 | `calculateRevenue()` 메서드를 엔티티에 추가 | 캡슐화, 일관된 패턴 |
| `SalesRecord` | 판매 매출 계산 로직 분산 | `calculateRevenue()` 메서드를 엔티티에 추가 | 일관성 확보 |

### 1.2 시간 범위 계산

| 대상 | 문제점 | 해결 | 효과 |
|------|--------|------|------|
| `DateTimeRange` | 일별/기간별 시간 범위 계산 패턴이 3번 반복 | 값 객체로 추출, 팩토리 메서드 제공 (`ofDay()`, `ofRange()`) | 중복 제거, 의도 명확화 |

---

## 2. 값 객체 (Value Object) 도입

원시 타입을 값 객체로 포장하여 유효성 검증과 비즈니스 로직 캡슐화.

### 2.1 단순 검증형 값 객체

| 값 객체 | 적용 엔티티 | 문제점 | 해결 | 효과 |
|---------|------------|--------|------|------|
| `PhoneNumber` | `Reservation`, `Customer` | 전화번호 형식 검증 없음 | 정규식 검증 + 정규화 (하이픈 제거) | 일관된 형식 보장, 포맷팅 메서드 제공 |
| `Email` | `Customer` | 이메일 형식 검증 없음 | 정규식 검증 + 소문자 정규화 | 형식 보장, 중복 방지 |
| `SiteNumber` | `Campsite` | 사이트 번호 형식 검증 없음 | `A-01` 형식 강제 + 대문자 정규화 | 형식 일관성 |
| `ConfirmationCode` | `Reservation` | 확인 코드 생성/검증 로직 분산 | 6자리 영숫자 검증 + 생성 로직 캡슐화 | 코드 생성 중앙화 |

### 2.2 비즈니스 로직 포함 값 객체

| 값 객체 | 적용 엔티티 | 문제점 | 해결 | 효과 |
|---------|------------|--------|------|------|
| `Stock` | `Product` | 재고 음수 방지, 조작 로직 분산 | 불변 객체 + `decrease()`, `increase()`, `hasEnough()` 메서드 | 재고 무결성 보장, 로직 캡슐화 |
| `StayPeriod` | `Reservation` | 숙박 기간 계산 로직 분산 | 종료일 >= 시작일 검증 + `calculateNights()` 메서드 | 날짜 유효성 보장 |
| `ReservationTiming` | `Reservation` | 예약일과 숙박 기간 관계 검증 없음 | `StayPeriod` 조합 + 예약일 <= 시작일 검증 | 복합 검증, 값 객체 조합 |

---

## 3. 인터페이스 및 Enum 개선

공통 행위 추출과 Enum에 로직 추가.

### 3.1 공통 인터페이스 추출

| 인터페이스 | 구현체 | 문제점 | 해결 | 효과 |
|-----------|--------|--------|------|------|
| `RevenueSource` | `Reservation`, `RentalRecord`, `SalesRecord` | 매출 계산 기능이 있지만 공통 타입 없음 | `calculateRevenue()` 인터페이스 추출 | 다형성 활용, 확장 용이 |

### 3.2 Enum에 로직 추가

| Enum | 문제점 | 해결 | 효과 |
|------|--------|------|------|
| `ReservationStatus` | 최종 상태 판단 로직 분산 | `isFinal()` 메서드 추가 (CANCELLED, CHECKED_OUT, REJECTED) | 상태 변경 가능 여부 중앙화 |
| `ProductType` | 대여/판매 가능 여부 판단 분산 | `isRentable()`, `isSellable()` 메서드 추가 | 타입별 행위 캡슐화 |

---

## 4. 계층 구조 개선

Controller → Service → Repository 계층 준수.

### 4.1 계층 분리

| 컨트롤러 | 문제점 | 해결 | 효과 |
|---------|--------|------|------|
| `ConsoleCampsiteController` | Repository 직접 의존 | Service 의존으로 변경 | 계층 준수, 트랜잭션 일관성 |
| `ConsoleProductController` | Repository 직접 의존 | Service 의존으로 변경 | 비즈니스 로직 분리 |
| `ConsoleReservationController` | Repository 직접 의존 | Service 의존으로 변경 | 테스트 용이성 향상 |

### 4.2 DTO 타입 안전성

| 대상 | 문제점 | 해결 | 효과 |
|------|--------|------|------|
| Controller 파라미터 | `Map<String, String>` 수동 파싱 | 타입 안전 DTO + `@ModelAttribute` | 자동 타입 변환, 예외 처리 제거 |

### 4.3 JPQL 쿼리 수정

| 대상 | 문제점 | 해결 | 효과 |
|------|--------|------|------|
| `ReservationRepository` | 값 객체 도입 후 쿼리 경로 불일치 | Embedded 객체 경로로 수정 (`r.timing.reservationDate`) | 값 객체 구조 반영 |

---

## 5. 예외 처리 개선

도메인 예외 체계화 및 i18n 지원.

### 5.1 ErrorCode 체계

| 구분 | 문제점 | 해결 | 효과 |
|------|--------|------|------|
| 예외 메시지 | 도메인 코드에 메시지 하드코딩 | `ErrorCode` 인터페이스 + Enum 구현체 | 메시지 중앙 관리 |
| HTTP 상태 | 모든 도메인 예외가 동일 상태 코드 | ErrorCode에 `HttpStatus` 포함 | 예외별 적절한 상태 코드 |
| 도메인명 | 예외 메시지에 도메인명 하드코딩 | `forClass()` 메서드로 클래스 기반 도메인명 조회 | 자동 매핑, 일관성 |

### 5.2 ErrorCode 분류

| ErrorCode | 용도 | HTTP Status |
|-----------|------|-------------|
| `CommonErrorCode` | 필수값, 유효성, 최소값 등 공통 | 400 Bad Request |
| `StockErrorCode` | 재고 부족 | 409 Conflict |
| `ReservationErrorCode` | 예약 상태 변경 불가, 날짜 오류 | 400/409 |
| `ProductErrorCode` | 대여/판매 불가 | 400 Bad Request |
| `RentalErrorCode` | 이미 반납됨 | 409 Conflict |

---

## 6. 테스트 개선

테스트 구조화 및 커버리지 관리.

### 6.1 테스트 분류

| 구분 | 대상 | 패턴 | 효과 |
|------|------|------|------|
| 값 객체 단위 테스트 | `Stock`, `PhoneNumber`, `Email` 등 | `@ParameterizedTest` + `@CsvSource` | 경계값 검증, 데이터 기반 테스트 |
| Enum 단위 테스트 | `ReservationStatus`, `ProductType` | `@EnumSource` | 회귀 방지, 문서화 역할 |
| 엔티티 단위 테스트 | `Product`, `Reservation`, `RentalRecord` | 도메인 로직 검증 | 비즈니스 규칙 보장 |
| 동시성 통합 테스트 | `SalesService`, `RentalService` | `@SpringBootTest` + 멀티스레드 | 동시성 문제 탐지 |

### 6.2 테스트 인프라

| 항목 | 문제점 | 해결 | 효과 |
|------|--------|------|------|
| 커버리지 | 커버리지 측정 없음 | JaCoCo 설정 | 시각화, 품질 관리 |

---

## 7. 동시성 이슈 분석

멀티스레드 환경에서 발생 가능한 문제 식별 및 검증.

### 7.1 발견된 문제

| 순위 | 문제 | 위치 | 증상 |
|------|------|------|------|
| #1 | 재고 감소 시 락 없음 | `SalesService`, `RentalService` | Lost Update → 재고 불일치 |
| #2 | 대여 반납 시 락 없음 | `RentalService.markAsReturned()` | 이중 반납 → 재고 과다 증가 |
| #3 | 예약 중복 체크 락 없음 | `ReservationRepository` | 동일 날짜 이중 예약 |

### 7.2 테스트 결과

| 테스트 | 예상 | 실제 | 문제 유형 |
|--------|------|------|----------|
| 재고 10개, 동시 10개 구매 | 재고 0 | 재고 6 | Lost Update |
| 재고 10개, 동시 20개 구매 | 성공 10 | 성공 20 | 과다 판매 |
| 동시 10회 반납 요청 | 성공 1 | 성공 8 | 이중 처리 |
| 재고 5개, 동시 10개 대여 | 성공 5 | 성공 10 | 과다 대여 |

### 7.3 해결 방안 (향후 작업)

| 방안 | 적용 대상 | 장점 | 단점 |
|------|----------|------|------|
| Pessimistic Lock | 재고 조회 | 확실한 동시성 제어 | DB 락 경합 |
| Optimistic Lock (`@Version`) | 엔티티 | 락 대기 없음 | 재시도 로직 필요 |
| 분산 락 (Redis) | 분산 환경 | 스케일 아웃 지원 | 인프라 복잡도 증가 |

---

## 8. 향후 개선 포인트

### 완료

- [x] 원시값을 값 객체로 포장
- [x] 관련 데이터 묶기 (`StayPeriod`, `ReservationTiming`)
- [x] 공통 인터페이스 추출 (`RevenueSource`)
- [x] Enum에 로직 추가
- [x] Controller 계층 분리
- [x] DTO 타입 안전성 확보
- [x] JPQL 쿼리 수정
- [x] 테스트용 클래스 분리
- [x] Enum/VO/Entity 단위 테스트 추가
- [x] 동시성 통합 테스트 추가
- [x] JaCoCo 설정

### 미완료

- [ ] 매직 넘버 `"50000"` 상수화 또는 설정값 분리
- [ ] 동시성 문제 해결 (Pessimistic/Optimistic Lock)
- [ ] 예약 중복 방지 로직 추가