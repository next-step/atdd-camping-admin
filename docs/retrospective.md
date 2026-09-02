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
- 입력값 검증 정책은 T-3, 콘솔 수정과 REST의 재고 외 필드 영속화는 T-4로 분리해 이번 구현에서 다루지 않았다.

### 다음 저장소에서도 반복할 것

- 수정 API는 수정 응답과 후속 조회를 한 쌍으로 검증하고, 테스트에서 실제로 부딪힌 로그인·데이터 준비·격리 방법을 `test-guide.md`에 한 줄로 남긴다.

## T-2 작업 회고

### 겪은 일에서 붙인 조건

- 화면마다 매출이 다르다는 증상만으로는 무엇을 비교해야 하는지 정할 수 없었다. 같은 거래를 고정하고 일별·기간 요약·상세에서 귀속일과 금액이 모두 같은지 확인하는 조건을 `acceptance-criteria.md`와 ATDD 스킬에 추가했다.
- 예약에는 `reservationDate`, `createdAt`, `startDate`처럼 날짜 후보가 여러 개 있었다. 잘못된 필드를 사용해도 우연히 통과하지 않도록 세 날짜를 서로 다르게 준비하고, 예약 거래는 확인을 거쳐 `reservationDate`에 귀속하기로 정했다.
- 매출 리포트는 저장된 모든 거래를 합산해 초기 데이터가 테스트 결과에 섞였다. 테스트마다 전용 거래만 남기고 끝나면 초기 데이터를 복원하는 `@Sql` 격리 방식을 `test-guide.md`에 추가했다.

### AI가 놓친 조건

#### 처음 놓친 것

- 화면별 총액 차이에 집중해 같은 거래의 귀속일과 금액을 식별자 기준으로 세 경계에서 대조해야 한다는 조건을 명확히 잡지 못했다.
- 예약을 어느 날짜에 귀속할지 요구사항만으로 정하지 않고 확인해야 한다는 점을 지나쳤다.

#### 고친 뒤에도 다시 놓친 것

- 첫 테스트 초안은 하루짜리 격리 데이터의 합계만 다뤄, 직접 실측한 여러 날 기간의 유형별 합계와 전체 합계를 그대로 검증하지 못했다.
- 예약과 연결된 대여가 연결 예약일이 아니라 대여 `createdAt`에 귀속되는지 구분하지 않았다. 연결 예약일과 대여 생성일을 다르게 둔 시나리오와 테스트를 추가했다.

### 조건을 산출물에서 확인한 결과

- 확인한 조건은 예약 거래를 `reservationDate`에 귀속하고, 같은 거래의 날짜와 금액을 세 리포트에서 일치시키는 것이다.
- `shouldAttributeSameReservationToReservationDateAcrossRevenueReports`는 일별·기간 요약의 예약 금액과 상세의 예약 #2001 금액 및 `occurredAt`을 함께 검증한다.
- `t2-revenue-fixture.sql`은 예약 #2001의 예약일을 2026-08-04, 생성일을 2026-08-03, 입실일을 2026-08-05로 준비한다. 잘못된 날짜 필드를 선택하면 귀속일 단언이 실패한다.
- `shouldAttributeSalesAndLinkedRentalToCreatedAtAcrossRevenueReports`는 연결 예약일 2026-08-28과 대여 생성일 2026-09-01을 구분해 일별·기간 요약·상세를 모두 검증한다.

### Red 테스트 보존과 Green 확인

- Red 전용 커밋 대신 구현 직전 인수 테스트의 파일 해시 `6bf52edf6001ff87a3b421b8bd9045595b75a96a`를 비교 기준으로 남겼다.
- 구현 전 `RevenueReportAcceptanceTest`는 4개 중 3개가 통과하고 1개가 실패했다. 예약 상세의 실제 표시일은 2026-08-03이었고 기대 귀속일은 2026-08-04였다.
- 기간 상세가 예약을 `reservationDate`로 선택하면서 응답의 `occurredAt`에는 `createdAt`을 넣던 한 지점을 `reservationDate.atStartOfDay()`로 변경했다.
- 구현 후 대상 인수 테스트 4개와 전체 테스트 7개가 모두 통과했다.
- 구현 후 인수 테스트의 파일 해시도 `6bf52edf6001ff87a3b421b8bd9045595b75a96a`로 같아 구현 과정에서 테스트를 바꾸지 않았음을 확인했다.

### AI 보고와 판정 단계의 직접 실행 결과 대조

- 구현 단계는 `./gradlew test --tests com.camping.admin.RevenueReportAcceptanceTest --rerun-tasks`에서 대상 테스트 4개, `./gradlew test --rerun-tasks`에서 전체 테스트 7개가 통과했다고 보고했다.
- 2026-09-02 (Asia/Seoul) 판정 단계에서 같은 두 명령을 다시 직접 실행했고 모두 `BUILD SUCCESSFUL`이었다.
- 판정 단계의 테스트 결과 XML은 `RevenueReportAcceptanceTest`가 `tests="4"`, `failures="0"`, `errors="0"`, `ProductUpdateAcceptanceTest`가 `tests="3"`, `failures="0"`, `errors="0"`이었다. 구현 단계 보고와 판정 단계 실행 결과가 일치했다.

### 절차를 알아서 빨라진 것

- T-1에서 정한 실제 로그인 JWT와 `@Sql` 격리 방식을 재사용해 인증과 데이터 초기화 방법을 다시 탐색하지 않았다.
- 귀속일 후보를 먼저 분리하고 실패값을 실측해 둔 덕분에 기간 상세의 `occurredAt` 한 지점으로 변경 범위를 빠르게 좁혔다.

### 이번에 덜 판 것

- T-2는 같은 거래가 리포트마다 같은 날짜와 금액으로 보이는지만 다뤘다.
- 예약 1박당 금액, 판매·대여 금액의 기준, 예약일 누락, 취소·반납 거래와 기간 경계의 포함 정책은 T-5로 분리했다.

### 다음 저장소에서도 반복할 것

- 같은 객체가 여러 경계에 나타나는 규칙은 식별자를 고정하고, 후보 날짜와 값의 출처를 서로 다르게 준비해 각 경계의 날짜와 금액을 직접 대조한다.
