Feature: 예약 관리

  Background:
    Given 관리자가 로그인되어 있다

  Scenario: 관리자가 예약 상태를 성공적으로 변경한다
    Given "CONFIRMED" 상태의 예약이 등록되어 있다
    When 관리자가 해당 예약의 상태를 "CANCELLED"로 변경하면
    Then 예약 상태가 "CANCELLED"로 변경된다