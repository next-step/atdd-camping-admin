# 프로젝트 안내

## 서비스 및 도메인

- 초록 캠핑장 체인의 관리자가 대시보드와 REST API로 사이트, 예약, 상품, 대여, 판매 및 매출을 통합 관리하는 Spring Boot 서비스다.
- `Campsite`는 캠핑 사이트와 수용 인원을, `Reservation`은 고객명·숙박 기간·예약 상태와 배정 사이트를 나타내며 `Customer`는 별도의 고객 연락처 정보를 담는다.
- `Product`는 판매용 또는 대여용 상품과 가격·재고를 나타내고, `RentalRecord`는 예약 연계 여부와 반납 상태를 기록한다.
- `SalesRecord`는 상품 판매 수량과 금액을 기록하며, 예약·판매·대여 기록을 합산해 일별 및 기간별 매출 보고서를 제공한다. 관리자 기능은 JWT 로그인이 필요하다.

## 저장소 구조

- `src/main/java/com/camping/admin/controller`는 관리자 REST API, `web`은 콘솔 화면 요청을 처리한다.
- `domain`은 엔티티와 열거형, `repository`는 영속화 경계, `service`는 상품·판매·대여 업무 로직을 담는다.
- `src/main/resources`에는 애플리케이션 설정, 초기 데이터, 화면 템플릿과 정적 자원이 있다.
- `src/test`에는 외부 HTTP 경계부터 검증하는 인수 테스트를 둔다.

## 저장소 작업 방식과 문서

- 이 저장소는 ATDD 학습용 레거시 시스템이며 의도적인 복잡성과 중복이 있으므로, 기존 동작을 먼저 인수 테스트로 확인하고 범위를 좁혀 점진적으로 변경한다.
- 원칙은 `docs/principles.md`, 작업 순서와 이 저장소에 매인 실행 정보는 `docs/plan.md`를 따른다.
- 티켓별 기준은 `docs/acceptance-criteria.md`, 테스트 제약은 `docs/test-guide.md`, 후속 작업은 `docs/tickets.md`, 작업에서 배운 점은 `docs/retrospective.md`에 남긴다.
