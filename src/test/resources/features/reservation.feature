Feature: 예약 상태 변경
  Scenario: 관리자가 예약 상태를 변경하면, 예약의 상태가 해당 상태로 변경된다.
    Given 관리자가 로그인을 한다.
    When 관리자가 예약 상태를 "CONFIRMED"로 변경한다.
    Then 예약 상태가 변경된다.