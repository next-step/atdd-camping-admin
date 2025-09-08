Feature: 예약 취소

  Scenario: 사용자가 예약한 건을 관리자가 취소하면 성공한다
    Given 사용자가 예약을 했다
    When 관리자가 예약 1을 취소했다
    Then 예약은 취소 상태다
