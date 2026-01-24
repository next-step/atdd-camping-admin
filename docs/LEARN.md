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