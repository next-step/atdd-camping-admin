Feature: 예약 관리

  Scenario: 새로운 예약을 생성한다
    Given 사용 가능한 캠프사이트가 있다
    When 관리자가 새로운 예약을 생성한다
    Then 예약 생성에 성공한다

  Scenario: 이미 예약된 날짜에 예약을 시도하면 실패한다
    Given 사용 가능한 캠프사이트가 있다
    And 해당 캠프사이트에 예약이 존재한다
    When 관리자가 중복된 날짜로 예약을 생성한다
    Then 예약 생성에 실패한다

  Scenario: 존재하지 않는 캠프사이트에 예약을 시도하면 실패한다
    When 관리자가 존재하지 않는 캠프사이트로 예약을 생성한다
    Then 예약 생성에 실패한다

  Scenario: 과거 날짜로 예약을 시도하면 실패한다
    Given 사용 가능한 캠프사이트가 있다
    When 관리자가 과거 날짜로 예약을 생성한다
    Then 예약 생성에 실패한다

  Scenario: 확정된 예약을 체크인 후 체크아웃 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CHECKED_IN" 상태로 변경한다
    And 예약을 "CHECKED_OUT" 상태로 변경한다
    Then 예약 상태가 "CHECKED_OUT"으로 변경된다

  Scenario: 예약 정보 없이 상태 변경을 시도하면 오류가 발생한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약 상태 변경 정보를 제공하지 않고 요청한다
    Then 상태 변경이 실패한다

  Scenario: 존재하지 않는 예약의 상태를 변경하려고 하면 오류가 발생한다
    Given 존재하지 않는 예약 번호를 사용한다
    When 예약을 "CHECKED_OUT" 상태로 변경한다
    Then 상태 변경이 실패한다

  Scenario Outline: 확정된 예약을 <상태>로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "<상태>" 상태로 변경한다
    Then 예약 상태가 "<상태>"으로 변경된다

    Examples:
      | 상태         |
      | CHECKED_IN   |
      | CHECKED_OUT  |
      | CANCELLED    |
      | CONFIRMED    |

