# 🚨 예외 처리 시스템 가이드

## 📋 개요

초록 캠핑장 관리자 시스템의 예외 처리는 **비즈니스 도메인별**로 구분된 커스텀 예외와 **일관된 응답 형태**를 제공합니다.

## 🏗️ 예외 처리 아키텍처

```
Service Layer (비즈니스 로직)
    ↓ 커스텀 예외 발생
GlobalExceptionHandler (중앙 집중 처리)
    ↓ HTTP 상태 코드 매핑
클라이언트 (일관된 에러 응답)
```

## 📊 커스텀 예외 분류

### 🔴 **비즈니스 로직 에러** → `500 Internal Server Error`

| 예외 클래스 | 발생 상황 | 메시지 예시 |
|-----------|---------|-----------|
| `ProductNotRentalException` | 판매상품을 렌탈 시도 | "Product is not a rental item." |
| `InsufficientStockException` | 재고 부족 | "Not enough stock for product 랜턴" |
| `RentalAlreadyReturnedException` | 이미 반납된 대여 반납 시도 | "This item has already been returned." |

### 🟡 **엔티티 Not Found 에러** → `400 Bad Request`

| 예외 클래스 | 발생 상황 | 메시지 예시 |
|-----------|---------|-----------|
| `ProductNotFoundException` | 존재하지 않는 상품 조회 | "Cannot find product with id: 999" |
| `ReservationNotFoundException` | 존재하지 않는 예약 조회 | "Cannot find reservation with id: 999" |
| `RentalNotFoundException` | 존재하지 않는 대여기록 조회 | "Cannot find rental record with id: 999" |

## 🎯 HTTP 상태 코드 매핑 전략

### ✅ **올바른 매핑 원칙**

- **500 에러**: **서버 내부의 비즈니스 로직 실패**
  - 재고 부족, 잘못된 상품 타입 등
  - 클라이언트 요청은 올바르지만 서버에서 처리할 수 없는 상황

- **400 에러**: **클라이언트의 잘못된 요청**
  - 존재하지 않는 리소스 참조
  - 필수 파라미터 누락, 잘못된 형식

## 📁 파일 구조

```
src/main/java/com/camping/admin/exception/
├── GlobalExceptionHandler.java          # 중앙 예외 처리기
├── ProductNotFoundException.java        # 상품 관련
├── ProductNotRentalException.java      # 상품 타입 관련
├── InsufficientStockException.java     # 재고 관련
├── ReservationNotFoundException.java   # 예약 관련
├── RentalNotFoundException.java        # 대여 관련
└── RentalAlreadyReturnedException.java # 대여 상태 관련
```

## 🔧 사용법

### 1️⃣ **서비스에서 커스텀 예외 던지기**

```java
// ❌ 기존 방식 (Generic Exception)
throw new IllegalArgumentException("Product is not a rental item.");

// ✅ 새로운 방식 (Domain Specific Exception)
throw new ProductNotRentalException("Product is not a rental item.");
```

### 2️⃣ **GlobalExceptionHandler에서 자동 처리**

```java
@ExceptionHandler(ProductNotRentalException.class)
public ResponseEntity<ApiResponse<Void>> handleBusinessLogicException(RuntimeException ex) {
    return ResponseEntity.status(500)
        .body(ApiResponse.error("Internal Server Error", ex.getMessage()));
}
```

## 📋 현재 상태 및 마이그레이션 체크리스트

### ✅ **완료된 작업**
- [x] 모든 커스텀 예외 클래스 생성
- [x] `RentalService` 커스텀 예외 적용
- [x] `ProductService` 커스텀 예외 적용
- [x] `ReservationService` 커스텀 예외 적용
- [x] `GlobalExceptionHandler` HTTP 상태 코드 매핑

### 🔄 **진행 중인 작업**
- [ ] `ApiResponse<T>` 제네릭 응답 객체 도입
- [ ] `ErrorResponse` DTO 표준화
- [ ] Map 기반 응답을 DTO 기반으로 마이그레이션

### 🎯 **향후 개선 계획**
- [ ] 예외별 상세한 에러 코드 도입 (E001, E002...)
- [ ] 다국어 지원을 위한 메시지 리소스 분리
- [ ] 로깅 전략 수립 (예외별 로그 레벨)
- [ ] 모니터링을 위한 메트릭 수집

## 🚀 **응답 형태 진화**

### **Phase 1**: Map 기반 응답 (현재)
```json
{
  "error": "Internal Server Error",
  "message": "Product is not a rental item.",
  "status": 500
}
```

### **Phase 2**: 제네릭 DTO 기반 응답 (목표)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "PRODUCT_NOT_RENTAL",
    "message": "Product is not a rental item.",
    "details": {
      "productId": 2,
      "productType": "SALE"
    }
  },
  "timestamp": "2025-01-14T15:30:00"
}
```

## 📈 **성능 모니터링**

### **예외 발생 빈도 추적**
- `ProductNotRentalException`: 대여 요청 중 판매상품 선택
- `InsufficientStockException`: 재고 부족으로 인한 대여/판매 실패
- `*NotFoundException`: 잘못된 ID로 인한 조회 실패

### **개선 지표**
- 500 에러 비율 감소 (비즈니스 로직 개선으로)
- 400 에러 비율 감소 (프론트엔드 검증 강화로)
- 평균 응답 시간 개선 (예외 처리 최적화로)

---

## 🔗 **관련 문서**
- [API 레퍼런스](./api-reference.md)
- [테스트 개발 가이드](./testing-guide.md)
- [프로젝트 아키텍처](./architecture.md)

---

*📅 최종 업데이트: 2025-01-14*
*🔄 이 문서는 프로젝트와 함께 자동으로 업데이트됩니다*