Feature: 예약 상태 변경
  Scenario: 관리자가 예약을 취소한다
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "CANCELLED" 상태로 변경한다
    Then 예약은 취소된다

  Scenario: 존재하지 않는 예약을 변경하는경우 실패한다.
    Given 예약이 존재하지 않는다
    When 관리자가 예약을 "CANCELLED" 상태로 변경한다
    Then 변경은 실패한다

  Scenario: 상태값을 입력하지 않은 경우 변경되지 않는다.
    Given 사용자가 예약을 했다.
    When 관리자가 상태값을 입력하지 않고 변경한다
    Then 예약 상태는 변경되지 않는다

  Scenario: 상태값을 빈문자열로 변경하는 경우 기존 상태를 유지한다.
    Given 사용자가 예약을 했다.
    When 관리자가 상태값을 빈문자열로 변경한다
    Then 예약 상태는 변경되지 않는다
