Feature: 예약 취소
  Scenario: 관리자가 예약을 취소한다
    Given 사용자가 예약을 했다.
    When 관리자가 예약을 취소한다.
    Then 예약은 취소된다.