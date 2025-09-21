Feature: 예약 상태 업데이트

  Scenario Outline: 예약 상태를 성공적으로 업데이트한다
    Given 예약이 존재한다
    When 관리자가 예약 상태를 '<status>'로 변경한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 '<status>'로 변경되었다

    Examples:
      | status      |
      | WAITING     |
      | PENDING     |
      | CONFIRMED   |
      | REJECTED    |
      | CHECKED_IN  |
      | CHECKED_OUT |
      | CANCELLED   |

  Scenario: 존재하지 않는 예약 ID로 상태 업데이트를 시도하면 실패한다
    Given 예약이 존재하지 않는다.
    When 관리자가 예약 상태를 변경한다
    Then 예약 상태 업데이트가 실패한다

  ## TODO 상태값이 유효하지 않은 경우에 테스트 통합하기 (로직도 제거)
  Scenario: 빈 요청 본문으로 예약 상태 업데이트를 시도하면 실패한다
    Given 예약이 존재한다
    When 관리자가 빈 요청 본문으로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

  ## TODO: 상태값이 유효하지 않을 경우 fail 처리하도록 정책 수정 필요
  Scenario Outline: 상태값을 빈문자열 혹은 공백으로 지정하여 상태값 변경 시도시, 실패한다.
    Given 예약이 존재한다
    When 관리자가 '<statusType>' 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

    Examples:
      | statusType | description |
      | ""         | 빈 문자열 상태값   |
      | null       | null 상태값    |
