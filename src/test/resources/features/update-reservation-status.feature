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

  Scenario Outline: 잘못된 예약 ID로 상태 업데이트를 시도하면 실패한다
    When 관리자가 예약 ID <reservationId>로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

    Examples:
      | reservationId | description   |
      | 999           | 존재하지 않는 예약 ID |
      | null          | null 예약 ID    |
      | -1            | 음수 예약 ID      |
      | 0             | 0 예약 ID       |

  Scenario: 빈 요청 본문으로 상태 업데이트를 시도하면 실패한다.
    Given 예약이 존재한다
    When 관리자가 본문 없이 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

  ## TODO: 상태값이 유효하지 않을 경우 fail 처리하도록 정책 수정 필요
  Scenario Outline: 상태값을 빈문자열 혹은 공백으로 지정후 업데이를 시도하면 기존 상태를 유지한다
    Given 예약이 존재한다
    And 예약의 현재 상태가 'CONFIRMED'이다
    When 관리자가 '<statusType>' 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CONFIRMED'로 유지되었다

    Examples:
      | statusType | description |
      | ""         | 빈 문자열 상태값   |
      | null       | null 상태값    |

  ## TODO: 겹침 with 빈 요청 본문으로 상태 업데이트를 시도하면 실패한다.
  Scenario: null 요청 본문으로 상태 업데이트를 시도하면 실패한다.
    Given 예약이 존재한다
    When 관리자가 null 요청 본문으로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다
