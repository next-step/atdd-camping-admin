Feature: 예약 관리
  Background:
    Given 예약 상태가 "CONFIRMED" 인 예약이 있다.

  Scenario: 관리자가 모든 예약을 조회할 수 있다.
    Given 예약이 존재한다.
    When 관리자가 예약 목록을 조회한다.
    Then 예약 목록이 조회된다.

  Scenario: 관리자가 예약 상태를 변경하면, 예약의 상태가 해당 상태로 변경된다.
    When 관리자가 예약 상태를 "CONFIRMED" 로 변경한다.
    Then 예약 상태가 "CONFIRMED" 로 변경된다.

  Scenario: 관리자가 존재하지 않는 예약의 예약 상태를 변경하면, 예약 상태 변경이 실패한다.
    Given  존재하지 않는 예약이 있다.
    When 관리자가 예약 상태를 "CONFIRMED" 로 변경한다.
    Then 예약 상태 변경이 실패한다.

  Scenario: 관리자가 동일한 예약 상태로 기존 예약을 수정하면, 예약의 상태가 유지된다.
    When 관리자가 예약 상태를 동일 상태로 변경한다.
    Then 예약 상태가 유지된다.