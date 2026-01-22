## Stpe 1: Cucumber를 활용한 인수 테스트 구축

> 먼저 제공된 기능 후보 중 하나를 선정하여 Gherkin과 Cucumber로 최소 안전벨트를 만듭니다. 선정 근거는 가벼운 리스크 판단으로 기록합니다.

```text
핵심 활동:

기능 후보 1가지를 선택하고 보호 대상으로 확정
Cucumber 베이스라인 세팅: JUnit Runner, features/, Step Definitions, RestAssured 기본 설정
선정 기능의 Happy Path 시나리오를 작성하고 통과
```

요구사항
1. 기능 선택
   - [ ] 아래 "선택형 기능 후보(실제 엔드포인트)"에서 1가지를 선택하세요.
   - [ ] 가볍게 리스크를 판단하여 해당 기능을 첫 보호 대상으로 확정하세요.
     - 선택형 기능 후보:
       - 예약 상태 변경: PATCH /admin/reservations/{id}/status (본문: { "status": "..." })
       - 예약 목록 조회: GET /admin/reservations
       - 상품 생성: POST /admin/products
       - 캠프사이트 생성: POST /admin/campsites
       - 대여 생성: POST /admin/rentals
     
> 기능이 정상적으로 동작하지 않을 경우 정상적으로 동작하도록 수정하고 진행해주세요!     

2. Cucumber 테스트 환경 구성
   - [ ] 테스트를 진행할 애플리케이션은 별도 프로세스로 기동하고(기본 http://localhost:8080), 테스트는 RestAssured를 이용하여 HTTP로 호출합니다.
     - 이 때 필요한 환경을 구축합니다.
       - 테스트 러너 클래스
3. Gherkin 시나리오 작성(선택 기능의 Happy Path)
   - [ ] features/ 하위에 Happy Path를 1개 작성합니다.
4. 인수 테스트 구현
   - [ ] Step 구현 위치: src/test/java/com/camping/admin/steps
5. 추가 구현
   - [ ] 필요 시 상태를 관리하는 별도의 객체를 만들거나, 각종 팩토리 등 테스트 도구로 필요한 로직을 구현합니다.