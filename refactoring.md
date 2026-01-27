#  API 리팩토링

## 개요
- API를 계층과 객체의 책임/역할 기반으로 리팩토링

---

## 1) 예약 변경 기능 
**[PATCH] /api/admin/reservations/{reservationId}/status**

## 기존 코드 문제점
### 1. Controller가 비즈니스 로직 수행
```java
@PatchMapping("/{reservationId}/status")
public ResponseEntity<ReservationResponse> updateReservationStatus(
        @PathVariable Long reservationId,
        @RequestBody Map<String, Object> body) {
    // Controller에서 직접 Repository 호출
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(...);

    // Controller에서 비즈니스 로직 수행
    reservation.setStatus(statusValue);
    reservationRepository.save(reservation);
    ...
}
```

### 2. 타입 안전성 부재
- `Map<String, Object>`로 요청을 받아 런타임에 타입 오류 발생 가능
- status 값이 String으로 관리되어 잘못된 값 입력 가능

### 3. 불필요한 분기문
- null 체크, 빈 문자열 체크 등 복잡한 분기 로직

### 4. 도메인 캡슐화 부족
- Entity의 상태 변경이 setter로만 이루어짐

---

## 리팩토링 결과

#### 계층별 책임 분리

| 계층 | 책임 | 파일 |
|------|------|------|
| **Controller** | HTTP 요청/응답 처리 | `ReservationAdminController.java` |
| **Service** | 비즈니스 로직 (트랜잭션 관리) | `ReservationService.java` |
| **Entity** | 도메인 로직 (상태 변경) | `Reservation.java` |
| **DTO** | 데이터 전송 객체 | `UpdateReservationStatusRequest.java` |

---

## 변경된 코드

### Controller
- 요청을 받아 Service에 위임하고 응답 반환만 담당

### Service

- 트랜잭션 관리
- 엔티티 조회 및 상태 변경 위임

### Entity
- status를 Enum 타입으로 변경하여 타입 안전성 확보
- 상태 변경 로직을 도메인 메서드로 캡슐화

### Request DTO

- 타입 안전한 요청 바인딩
- 유효하지 않은 status 값은 역직렬화 단계에서 예외 발생

---

## 테스트 변경 사항

### 삭제된 테스트 시나리오

| 시나리오 | 삭제 이유 |
|---------|----------|
| 빈 요청을 보내면 잘못된 요청으로 처리된다 | Request DTO + `@Valid` 도입으로 Spring이 자동 검증 |
| 상태값을 지정하지 않으면 잘못된 요청으로 처리된다 | `@NotNull` validation으로 Spring이 자동 검증 |
| 유효하지 않은 상태값으로 변경하면 잘못된 요청으로 처리된다 | Enum 타입 도입으로 Jackson 역직렬화 단계에서 자동 검증 |
| 빈 문자열 상태값은 잘못된 요청으로 처리된다 | Enum 타입 도입으로 Jackson 역직렬화 단계에서 자동 검증 |

### 삭제 이유
- 기존 테스트들은 `Map<String, Object>`로 요청을 받을 때 필요했던 엣지 케이스 검증
- Request DTO + Enum 타입 도입으로 Spring/Jackson이 자동으로 검증 수행
- ATDD 관점에서 프레임워크 동작 검증보다 **핵심 비즈니스 로직**에 집중하는 것이 효과적

---

## 2) 상품 등록 기능
**[POST] /admin/products**

## 기존 코드 문제점

### 1. Controller가 비즈니스 로직 수행
```java
@PostMapping
public ResponseEntity<Product> createProduct(@RequestBody Map<String, Object> body) {
    // Controller에서 직접 Repository 호출
    Product saved = productRepository.save(newProduct);
    ...
}
```

### 2. 복잡한 타입 변환 로직
```java
Integer stockQuantity;
if (body.containsKey("stockQuantity")) {
    Object v = body.get("stockQuantity");
    if (v instanceof Number) {
        stockQuantity = ((Number) v).intValue();
    } else if (v == null) {
        stockQuantity = 0;
    } else {
        try {
            stockQuantity = Integer.valueOf(v.toString());
        } catch (Exception e) {
            stockQuantity = 0;
        }
    }
} else {
    stockQuantity = 0;
}
```
- 각 필드마다 반복되는 타입 변환 및 기본값 처리 로직
- 가독성 저하 및 유지보수 어려움

### 3. 엔티티 직접 노출
- 응답으로 Entity를 그대로 반환하여 내부 구조 노출

---

## 리팩토링 결과

#### 계층별 책임 분리

| 계층 | 책임 | 파일 |
|------|------|------|
| **Controller** | HTTP 요청/응답 처리 | `ProductAdminController.java` |
| **Service** | 비즈니스 로직 (트랜잭션 관리) | `ProductService.java` |
| **Request DTO** | 요청 바인딩 + 기본값 처리 + Entity 변환 | `CreateProductRequest.java` |
| **Response DTO** | 응답 데이터 구성 | `ProductResponse.java` |

---

## 변경된 코드

### Controller
- 요청을 받아 Service에 위임하고 응답 반환만 담당

### Service
- 트랜잭션 관리
- Entity 생성 및 저장

### Request DTO
- 기본값 처리 로직을 DTO에 캡슐화
- `toEntity()` 메서드로 Entity 변환 책임 담당

### Response DTO
- Entity 내부 구조 노출 방지
- 정적 팩토리 메서드로 일관된 생성 방식 제공

---

## 테스트 변경 사항

### 삭제된 테스트 시나리오

| 시나리오 | 삭제 이유 |
|---------|----------|
| 유효하지 않은 상품 유형을 입력하면 기본값으로 처리된다 | Enum 타입 도입으로 Jackson 역직렬화 단계에서 자동 검증 |

### 삭제 이유
- 기존 코드는 `Map<String, Object>`로 요청을 받아 `ProductType.valueOf()` 실패 시 catch로 기본값 처리
- Request DTO + Enum 타입 도입 후에는 Jackson 역직렬화 단계에서 실패 → 400 응답
- 이 시나리오를 테스트하려면 raw JSON으로 요청해야 하는데, 이는 **프레임워크 동작 테스트**에 해당
- ATDD 관점에서 핵심 비즈니스 로직에 집중하는 것이 효과적

### Steps 변경 사항
- `buildCreateProductRequest()`: DTO를 생성하여 타입 안전한 방식으로 테스트 데이터 구성
- 타입 안전한 클라이언트 사용 패턴을 시뮬레이션

---


