# 1. 앱 기동 방식 차이

## 임베디드 - @SpringBootTest

테스트 실행 → Spring이 앱을 자동으로 띄움 (랜덤 포트) → 테스트 → 앱 종료
- 테스트가 알아서 앱을 띄우고 끔
- 포트가 매번 바뀜 (@LocalServerPort로 받아옴)

## 별도 프로세스

1. 터미널에서 ./gradlew bootRun (앱 수동 실행, 8080 고정)
2. 다른 터미널에서 ./gradlew test (테스트만 실행)
- 앱이 이미 떠있다고 가정하고 테스트
- 포트 8080 고정                                                                                                                                                     
                    

# 2. 예약1 취소 vs 예약 취소 
> 인수테스트이므로 1번이 예약 번호인지 모른다고 가정, SQL문 변경되거나 잘못 초기화 되는 경우 

# 3. Static 방식과 PicoContainer
## Static 방식 (현재)
```java
public class CommonContext {                                                                                                                                                                                                          
private static String adminToken;  // 전역 상태                                                                                                                                                                                   
}
```
- 장점: 간단함, 어디서든 접근                                                                                                                                                                                                           
- 단점:
  - 테스트 간 격리 문제 (이전 테스트 상태가 남을 수 있음)
  - 병렬 테스트 시 충돌   
## PicoContainer
- 시나리오마다 새 인스턴스 생성 

# 4. 외부 값 변경/영향 (DB) 가 인수테스트로 에러임이 밝혀지지 않았다 
- 단순히 응답 코드를 확인하기보다 직접 DB에서 값을 조회해봐야 할것같다

# 5. 컨트롤러와 도메인 사이의 예외 처리 책임
- Controller: "형식이 맞는가?" (400 Bad Request)
- Domain: "비즈니스적으로 유효한가?" (도메인 예외)                                                                                                                               
                                
# 6. DateTimeFormat
- @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)

# 7. JPA 영속성 컨텍스트와 Dirty Checking

## 영속성 컨텍스트 (Persistence Context)
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

## 엔티티 생명주기
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

## Dirty Checking 동작 원리
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

## flush 타이밍
영속성 컨텍스트 → DB 동기화 시점:
1. 트랜잭션 커밋 직전
2. JPQL 쿼리 실행 직전
3. `em.flush()` 직접 호출

## @Transactional 없으면?
```java
// @Transactional 없음
public void update(Long id) {
    Product p = repo.findById(id);  // 조회 직후 트랜잭션 종료 → 준영속
    p.setName("새이름");            // 그냥 메모리상 변경
    // DB 반영 안 됨!
}
```
`@Transactional`이 있어야 메서드 전체가 하나의 트랜잭션으로 묶여서 dirty checking 동작

## save() 불필요 예시
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