## T-1 작업 회고

### 겪은 일에서 붙인 조건

- 수정 응답에는 재고 `21`이 표시됐지만 후속 조회에서는 `20`이었다. 이 경험 때문에 저장 여부는 수정 응답이 아니라 후속 조회로 판정한다는 조건을 `acceptance-criteria.md`와 `atdd-acceptance`에 추가했다.
- 보호된 관리자 API 호출에는 JWT가 필요했고 테스트마다 상품 상태를 같은 값으로 되돌려야 했다. 이 때문에 실제 로그인으로 받은 Bearer 토큰을 사용하고, 전용 상품 ID를 `@Sql`로 초기화한다는 규칙을 `test-guide.md`와 `atdd-test`에 추가했다.
- 요청과 응답 전문을 모두 기록하니 인수 규칙이 잘 읽히지 않았다. 이 때문에 Given-When-Then을 중심으로 쓰고 판정에 필요한 상태 코드, 필드와 후속 조회값만 남긴다는 지시를 `plan.md`와 `atdd-acceptance`에 추가했다.

### AI가 놓친 조건

#### 처음 놓친 것

- 요구 대상인 REST `PUT /admin/products/{id}` 대신 콘솔 요청을 대상으로 잡았다.
- 수정 응답만 확인하고 후속 조회로 실제 저장 여부를 확인하지 않았다.

#### 고친 뒤에도 다시 놓친 것

- 인수 조건에 요청과 응답 전문을 남겨 규칙보다 예시가 더 크게 보이게 했다. Given-When-Then 시나리오와 판정에 필요한 값만 남기도록 수정했다.
- 이름만 수정하는 회귀 테스트에서 응답 본문의 이름을 단언했다. 응답 이름은 영속화를 증명하지 않고 이번 회귀 조건도 아니므로 제거하고, 수정 응답과 후속 조회에서 재고와 가격이 보존되는지만 확인하도록 수정했다.

### 조건을 산출물에서 확인한 결과

- 확인한 조건은 저장 여부를 수정 응답이 아니라 별도 조회 결과로 판정하는 것이다.
- `shouldPersistPositiveStockQuantity`와 `shouldPersistZeroStockQuantity`는 수정 응답을 확인한 뒤 `assertStoredProduct`를 호출한다.
- `assertStoredProduct`는 `GET /admin/products`를 호출하는 `findProduct`의 결과를 검증하므로, 후속 조회로 저장 여부를 판정한다는 조건이 실제 인수 테스트에 반영됐음을 확인했다.

### Red 테스트 보존과 Green 확인

- 인수 테스트는 커밋 `1e14c76`에 실패하는 상태로 남겼다.
- 구현 전에는 재고 `21` 시나리오가 실제 값 `20`, 재고 `0` 시나리오가 실제 값 `20`으로 예상한 저장 결과 단언에서 실패했다.
- 이름만 수정할 때 재고와 가격을 보존하는 회귀 테스트는 구현 전에도 통과했다.
- 구현 후 `ProductUpdateAcceptanceTest`와 전체 테스트가 모두 통과했다.
- `git diff --exit-code 1e14c76 -- src/test/java/com/camping/admin/ProductUpdateAcceptanceTest.java`의 종료 코드가 `0`이어서 구현 과정에서 1단계가 남긴 인수 테스트를 수정하지 않았음을 확인했다.

### AI 보고와 직접 실행 결과 대조

- AI는 `ProductUpdateAcceptanceTest` 3개와 전체 테스트 3개가 통과했다고 보고했다.
- 2026-09-01 23:51 (Asia/Seoul)에 다음 명령을 직접 실행했다.
  - `./gradlew test --tests com.camping.admin.ProductUpdateAcceptanceTest --rerun-tasks`: `BUILD SUCCESSFUL in 6s`
  - `./gradlew test --rerun-tasks`: `BUILD SUCCESSFUL in 5s`
- 직접 실행으로 생성된 테스트 결과는 `tests="3"`, `failures="0"`, `errors="0"`이었다. 대상 테스트와 전체 테스트가 통과했다는 AI 보고와 직접 실행 결과가 일치했다.

### 절차를 알아서 빨라진 것

- 로그인 JWT와 `@Sql` 격리 방식을 먼저 정해 인증과 데이터 준비 방법을 반복해서 탐색하지 않았다.

### 이번에 덜 판 것

- T-1을 REST 재고 수정으로 제한했다.
- 입력값 검증 정책은 T-2, 콘솔 수정과 REST의 재고 외 필드 영속화는 T-3으로 분리해 이번 구현에서 다루지 않았다.

### 다음 저장소에서도 반복할 것

- 수정 API는 수정 응답과 후속 조회를 한 쌍으로 검증하고, 테스트에서 실제로 부딪힌 로그인·데이터 준비·격리 방법을 `test-guide.md`에 한 줄로 남긴다.
