Feature: 관리자 예약 관리 기능

  Scenario: 사용자가 예약한 건을 관리자가 취소할 수 있다.
    Given 사용자가 예약을 했다
    When 관리자가 예약을 취소했다
    Then 예약은 취소 상태다
