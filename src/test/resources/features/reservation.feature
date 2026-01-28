Feature: 예약 상태 관리
  Background:
    Given 캠핑장에 'A-01' 사이트가 등록되어 있다
    And 관리자 로그인이 되어 있다

  Scenario: 예약 상태를 확정으로 변경한다
    Given 사이트 번호가 'A-01'인 캠핑장에 '홍길동'이 대기 상태로 예약되어 있다
    When 관리자가 '홍길동'의 예약을 확정하면
    Then 예약이 확정된다

  Scenario Outline: 예약 상태 변경 시 도메인 무결성 규칙을 검증한다
    Given 사이트 번호가 'A-01'인 캠핑장에 '홍길동' 고객이 '<old_status>' 상태로 예약되어 있다
    When 관리자가 '홍길동'의 예약 상태를 '<new_status>'으로 변경하면
    Then 요청이 성공한다

    Examples: 성공 케이스 (정상 흐름)
      | old_status     | new_status   | 설명              |
      | WAITING        | PENDING      | 대기에서 승인대기 |
      | PENDING        | CONFIRMED    | 승인대기에서 확정 |
      | CONFIRMED      | CHECKED_IN   | 확정에서 체크인   |