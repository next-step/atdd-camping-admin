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
