# 🎯 Value Objects Guide - Bounded Context 내에서의 용어 의미 지키기

## 📋 개요

이 문서는 캠핑장 관리자 시스템에서 **Domain-Driven Design (DDD)**의 핵심 원칙인 **Bounded Context** 내에서 각 용어의 의미를 명확하게 지키기 위한 **Value Objects** 사용 가이드입니다.

## 🎯 핵심 원칙

### 1️⃣ **용어의 명확성 (Ubiquitous Language)**
- **StockQuantity**: 상품의 재고 수량 (0 이상, 감소/증가 가능)
- **RecordQuantity**: 대여 기록의 수량 (1 이상, 불변)
- **각 컨텍스트마다 다른 비즈니스 규칙과 제약조건 적용**

### 2️⃣ **타입 안전성**
- 컴파일 타임에 잘못된 값 차단
- 도메인 규칙을 Value Object 생성 시점에서 검증
- 외부 시스템과의 인터페이스(DTO)는 원시 타입 유지

### 3️⃣ **불변성 (Immutability)**
- 모든 Value Objects는 불변 객체
- 변경이 필요한 경우 새로운 인스턴스 생성
- Thread-Safe 보장

## 📚 Value Objects 정의

### 🏪 StockQuantity (재고 수량)

#### 📄 **목적**
상품의 재고 수량을 나타내며, 재고 관리 도메인의 비즈니스 규칙을 캡슐화

#### 🔒 **제약조건**
- **최소값**: 0 (음수 불가)
- **비즈니스 로직**: 충분한 재고 확인, 재고 감소/증가

#### 🎯 **핵심 메서드**
```java
// 재고 감소 가능 여부 확인
boolean canDecrease(RecordQuantity decreaseAmount)

// 재고 감소 (불변성 유지)
StockQuantity decrease(RecordQuantity decreaseAmount)

// 재고 증가 (불변성 유지)
StockQuantity increase(RecordQuantity increaseAmount)

// 재고 가용성 확인
boolean isAvailable(RecordQuantity requiredAmount)

// 재고 비어있음 여부
boolean isEmpty()
```

#### 🚨 **예외 상황**
- **InvalidQuantityException**: 음수 값으로 생성 시도
- **InsufficientStockException**: 부족한 재고 감소 시도

---

### 📋 RecordQuantity (대여 기록 수량)

#### 📄 **목적**
대여 기록의 수량을 나타내며, 대여 도메인의 비즈니스 규칙을 캡슐화

#### 🔒 **제약조건**
- **최소값**: 1 (0 이하 불가)
- **비즈니스 로직**: 대여 수량 검증, 산술 연산

#### 🎯 **핵심 메서드**
```java
// 수량 비교
boolean isEqualTo(RecordQuantity other)
boolean isGreaterThan(RecordQuantity other)

// 산술 연산 (불변성 유지)
RecordQuantity add(RecordQuantity other)
RecordQuantity subtract(RecordQuantity other)
```

#### 🚨 **예외 상황**
- **InvalidQuantityException**: 0 이하 값으로 생성/연산 시도

## 🏗️ 아키텍처 패턴

### 📊 **계층별 사용법**

```
📱 Presentation Layer (Controller)
  ↓ Integer/String (JSON 호환성)

🔄 Application Layer (Service)
  ↓ Value Objects로 변환

🎯 Domain Layer (Entity/Aggregate)
  ↓ Value Objects 사용

📊 Infrastructure Layer (Repository)
  ↓ 자동 매핑 (@Embedded)
```

### 🔄 **변환 규칙**

#### 1️⃣ **DTO → Value Object (입력)**
```java
// Controller/Service Layer
public RentalResponse createRental(CreateRentalRequest request) {
    // DTO의 Integer를 도메인에서 Value Object로 변환
    RentalRecord rentalRecord = new RentalRecord(
        reservation,
        product,
        request.getQuantity() // Integer → RentalRecord 내부에서 RecordQuantity로 변환
    );
}

// Domain Layer
public RentalRecord(Reservation reservation, Product product, Integer quantity) {
    this.quantity = new RecordQuantity(quantity); // 여기서 검증 수행
}
```

#### 2️⃣ **Value Object → DTO (출력)**
```java
// Response DTO
private RentalResponse(RentalRecord rentalRecord) {
    this.quantity = rentalRecord.getQuantity().getQuantity(); // Value Object → Integer
}
```

## 📋 구현 체크리스트

### ✅ **Value Object 생성 시**
- [ ] 생성자에서 유효성 검증
- [ ] 불변 필드 (`final` 또는 불변 객체)
- [ ] `@Embeddable` 어노테이션 추가
- [ ] Lombok `@Getter`, `@NoArgsConstructor` 추가
- [ ] 비즈니스 메서드 구현 (comparison, calculation 등)

### ✅ **Entity 수정 시**
- [ ] `@Embedded` 어노테이션 추가
- [ ] `@AttributeOverride`로 컬럼명 매핑
- [ ] 생성자에서 Value Object 변환
- [ ] 비즈니스 메서드에서 Value Object 사용

### ✅ **Service Layer 수정 시**
- [ ] Value Object를 직접 사용 (Integer 추출하지 않음)
- [ ] 도메인 로직을 Entity/Value Object에 위임

### ✅ **테스트 작성 시**
- [ ] Value Object 단위 테스트 (생성, 검증, 예외)
- [ ] Entity 통합 테스트 (Value Object와의 협력)
- [ ] 경계값 테스트 (최소/최대/예외 케이스)

## 🚨 주의사항

### ❌ **하지 말아야 할 것들**

```java
// ❌ Service에서 Integer로 변환해서 사용
productService.decreaseStock(productId, quantity.getQuantity());

// ✅ Value Object 직접 전달
productService.decreaseStock(productId, quantity);
```

```java
// ❌ Entity에서 원시 타입 사용
private Integer stockQuantity;

// ✅ Value Object 사용
private StockQuantity stockQuantity;
```

### ✅ **올바른 사용법**

```java
// ✅ 도메인 로직을 Value Object에 캡슐화
StockQuantity newStock = currentStock.decrease(recordQuantity);

// ✅ 비즈니스 규칙을 Value Object에서 검증
if (stockQuantity.isAvailable(requiredQuantity)) {
    // 비즈니스 로직 수행
}
```

## 📈 Benefits (이점)

### 1️⃣ **타입 안전성**
- 컴파일 타임에 잘못된 값 차단
- IDE에서 자동완성 지원으로 개발 효율성 증대

### 2️⃣ **도메인 무결성**
- 비즈니스 규칙을 코드로 명확하게 표현
- 유효하지 않은 상태의 객체 생성 방지

### 3️⃣ **유지보수성**
- 비즈니스 규칙 변경 시 한 곳에서만 수정
- 코드의 의도가 명확하게 드러남

### 4️⃣ **테스트 용이성**
- Value Object 단위로 테스트 가능
- 복잡한 비즈니스 로직을 작은 단위로 분해

## 🔮 확장 가이드

### 새로운 Value Object 추가 시

1. **도메인 전문가와 용어 정의**
2. **비즈니스 규칙과 제약조건 식별**
3. **Value Object 클래스 생성**
4. **Entity 수정 (필드 타입 변경)**
5. **Service 로직 업데이트**
6. **포괄적인 단위 테스트 작성**
7. **문서 업데이트**

---

💡 **이 가이드를 따라 구현하면, Bounded Context 내에서 각 용어의 의미가 명확하게 지켜지고, 타입 안전하며 유지보수 가능한 도메인 모델을 구축할 수 있습니다.**