Feature: 예약 상태 변경
  Scenario: 관리자가 예약 상태를 변경한다
    Given 예약이 존재한다
    When 관리자가 예약 상태를 CHECKED_IN로 변경한다
    Then 예약 상태가 CHECKED_IN로 변경된다

  Scenario: 존재하지 않는 예약의 상태를 변경할 수 없다
    Given 관리자가 로그인되어 있다
    When 관리자가 존재하지 않는 예약의 상태를 변경한다
    Then 예약 상태 변경이 거부된다

  Scenario: 예약 상태를 CHECKED_OUT으로 변경할 수 있다
    Given 예약이 존재한다
    When 관리자가 예약 상태를 CHECKED_OUT로 변경한다
    Then 예약 상태가 CHECKED_OUT로 변경된다

  Scenario: 잘못된 상태값으로 변경할 수 없다
    Given 예약이 존재한다
    When 관리자가 예약 상태를 INVALID_STATUS로 변경한다
    Then 예약 상태 변경이 거부된다

  Scenario: 빈 상태값으로 변경할 수 없다
    Given 예약이 존재한다
    When 관리자가 예약 상태를 빈 값으로 변경한다
    Then 예약 상태 변경이 거부된다
