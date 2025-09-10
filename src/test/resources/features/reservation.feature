Feature: 예약 상태 변경

  Background:
    Given 관리자가 로그인했다

  Scenario: 확정된 예약을 체크인 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CHECKED_IN" 상태로 변경한다
    Then 예약 상태가 "CHECKED_IN"으로 변경된다

