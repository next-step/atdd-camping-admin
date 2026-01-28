# ATDD 학습 기록

---

# 기술 (Technology)

## Spring

### 앱 기동 방식 차이

#### 임베디드 - @SpringBootTest
테스트 실행 → Spring이 앱을 자동으로 띄움 (랜덤 포트) → 테스트 → 앱 종료
- 테스트가 알아서 앱을 띄우고 끔
- 포트가 매번 바뀜 (@LocalServerPort로 받아옴)

#### 별도 프로세스
1. 터미널에서 ./gradlew bootRun (앱 수동 실행, 8080 고정)
2. 다른 터미널에서 ./gradlew test (테스트만 실행)
- 앱이 이미 떠있다고 가정하고 테스트
- 포트 8080 고정

### DateTimeFormat
```java
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
```

### 컨트롤러와 도메인 사이의 예외 처리 책임
| 계층 | 책임 | HTTP 상태 |
|------|------|-----------|
| Controller | "형식이 맞는가?" | 400 Bad Request |
| Domain | "비즈니스적으로 유효한가?" | 도메인 예외 (409 등) |

---

## JPA

### 영속성 컨텍스트 (Persistence Context)
JPA가 엔티티를 관리하는 메모리 공간 (1차 캐시)
```
┌─────────────────────────────────────┐
│       영속성 컨텍스트 (1차 캐시)        │
│  ┌─────────┬─────────┬──────────┐  │
│  │ ID: 1   │ ID: 2   │ ID: 3    │  │
│  │ Product │ Product │ Product  │  │
│  │ (원본)  │ (원본)   │ (원본)   │  │
│  └─────────┴─────────┴──────────┘  │
└─────────────────────────────────────┘
              ↕ flush
         ┌─────────┐
         │   DB    │
         └─────────┘
```

### 엔티티 생명주기
```
비영속(new)  ──save()──→  영속(managed)  ──detach()──→  준영속(detached)
                              │
                          remove()
                              ↓
                          삭제(removed)
```
| 상태 | 설명 |
|------|------|
| 비영속 | `new Product()` - 그냥 자바 객체 |
| 영속 | `save()`, `findById()` 후 - 영속성 컨텍스트가 관리 |
| 준영속 | 트랜잭션 끝남 - 더 이상 관리 안 함 |

### Dirty Checking 동작 원리
```java
@Transactional
public void updateProduct(Long id, String name) {
    Product product = productRepository.findById(id);  // 1. 영속 상태 + 스냅샷 저장
    product.setName(name);                              // 2. 값 변경 (메모리)
    // save() 안 해도 됨!                                // 3. 트랜잭션 끝
}
```
1. `findById()` → DB 조회 → **영속성 컨텍스트에 원본 스냅샷 저장**
2. `setName()` → 엔티티 값 변경 (메모리상)
3. 트랜잭션 커밋 시 → **원본 스냅샷 vs 현재 엔티티 비교**
4. 다르면 → **자동으로 UPDATE 쿼리 생성**
```
스냅샷: { id: 1, name: "텐트" }
현재:   { id: 1, name: "침낭" }  ← 다름!
→ UPDATE products SET name='침낭' WHERE id=1
```

### flush 타이밍
영속성 컨텍스트 → DB 동기화 시점:
1. 트랜잭션 커밋 직전
2. JPQL 쿼리 실행 직전
3. `em.flush()` 직접 호출

### @Transactional 없으면?
```java
// @Transactional 없음
public void update(Long id) {
    Product p = repo.findById(id);  // 조회 직후 트랜잭션 종료 → 준영속
    p.setName("새이름");            // 그냥 메모리상 변경
    // DB 반영 안 됨!
}
```
`@Transactional`이 있어야 메서드 전체가 하나의 트랜잭션으로 묶여서 dirty checking 동작

### save() 불필요 예시
```java
// 불필요한 save() 호출
@Transactional
public Reservation updateStatus(Long reservationId, String status) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(...);
    reservation.updateStatus(status);
    reservationRepository.save(reservation);  // 불필요!
    return reservation;
}

// 올바른 방식
@Transactional
public Reservation updateStatus(Long reservationId, String status) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(...);
    reservation.updateStatus(status);
    return reservation;  // 트랜잭션 종료 시 자동 flush
}
```

---

## Cucumber

### Static 방식 vs PicoContainer

#### Static 방식
```java
public class CommonContext {
    private static String adminToken;  // 전역 상태
}
```
- 장점: 간단함, 어디서든 접근
- 단점:
  - 테스트 간 격리 문제 (이전 테스트 상태가 남을 수 있음)
  - 병렬 테스트 시 충돌

#### PicoContainer
- 시나리오마다 새 인스턴스 생성
- 테스트 격리 보장

---

## 테스트 설계

### "예약1 취소" vs "예약 취소"
> 인수테스트이므로 1번이 예약 번호인지 모른다고 가정
> SQL문 변경되거나 잘못 초기화 되는 경우 대비

### 외부 값 변경/영향 (DB)
- 단순히 응답 코드를 확인하기보다 **직접 DB에서 값을 조회**해봐야 함
- 응답만 보면 실제 DB 반영 여부를 알 수 없음

---

# 느낀점 (Reflections)

## 인수테스트 기반 리팩터링

---

## 도메인 주도 설계 (DDD)

### 값 객체(Value Object)의 장점
- 유효성 검증 로직 캡슐화
- 불변성 보장
- 의미 있는 도메인 용어 사용

### Tell, Don't Ask
- 서비스에서 getter로 값 꺼내서 계산 ❌
- 엔티티에게 계산을 요청 ✅
```java
// Bad: 서비스가 내부를 알아야 함
long nights = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());

// Good: 엔티티에게 위임
BigDecimal revenue = reservation.calculateRevenue();
```

### 계층 분리
```
Controller → Service → Repository → Entity
    ↑           ↑          ↑           ↑
   DTO        비즈니스    데이터     도메인
              로직       접근      로직
```
- Controller가 Repository 직접 사용 ❌
- 트랜잭션 관리는 Service에서


# 인수 테스트 기반으로 리팩터링을 하면서 느낀 점

- **Input / Output 형식 변경을 즉시 감지할 수 있다**
    - 리팩터링 과정에서 여러 필드를 하나의 객체로 묶어 객체화했는데,  
      응답이 `필드1, 필드2` → `object: { 필드1, 필드2 }` 형태로 바뀌면서  
      API 응답 스펙이 의도치 않게 변경되었다.
    - 인수 테스트 덕분에 외부 계약(API Contract) 변경을 즉시 인지할 수 있었고,  
      *“내부 구조 변경 ≠ 외부 인터페이스 변경”* 이라는 원칙을 다시 체감했다.

- **세세한 도메인 로직 검증에는 단위 테스트가 필수적이다**
    - 인수 테스트는 시나리오 단위의 검증에는 효과적이지만,  
      경계 조건이나 복잡한 분기 로직까지 모두 커버하기에는 한계가 있다.
    - 도메인 규칙, 계산 로직, 상태 전이와 같은 부분은  
      단위 테스트로 명확하게 분리해 검증하는 것이 유지보수 측면에서 훨씬 안정적이었다.

- **OO 테스트는 OO 단위로 작성하되, 테스트 케이스는 엣지 케이스 중심으로 설계해야 한다**
    - 테스트는 구현이 아닌 **책임과 역할(OO)** 을 기준으로 작성해야 리팩터링에 강하다.
    - 정상 케이스보다는 다음과 같은 엣지 케이스를 중심으로 구성했을 때 테스트의 가치가 더 크게 드러났다.
        - 경계값
        - 잘못된 입력
        - 상태 전이 실패
        - 예외 상황
    - 그 결과, 내부 구조 변경 시에도 테스트 수정 범위를 최소화할 수 있었다.
