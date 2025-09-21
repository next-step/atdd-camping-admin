Feature: 예약 상태 변경
  Scenario: 관리자가 예약 상태를 변경하면, 예약의 상태가 해당 상태로 변경된다.
    Given WATING 상태인 예약이 존재한다.
    When 관리자가 WATING 상태인 예약 상태를 PENDING 상태로 변경한다.
    Then 예약 상태가 PENDING 으로 변경된다.

  Scenario: 관리자가 존재하지 않는 예약의 상태를 변경하면, 예외가 발생한다.
    Given 존재하지 않는 예약 ID 9999 가 있다.
    When 관리자가 예약 ID 9999 의 상태를 PENDING 상태로 변경한다.
    Then 에러 응답이 발생한다.