Feature: 예약 취소 기능
  관리자는 고객의 예약을 취소할 수 있다.

  Background:
    Given 관리자로 로그인되어 있다

  Scenario: 확정된 예약을 취소한다
    Given "김철수" 고객의 "A-001" 캠프사이트 예약이 "CONFIRMED" 상태로 존재한다
    When 관리자가 해당 예약을 "CANCELLED" 상태로 변경한다
    Then 예약 상태가 "CANCELLED"로 변경된다