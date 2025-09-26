Feature: 예약 상태 변경
  Background:
    Given 관리자가 캠핑장을 생성한다

  Scenario: 관리자가 예약을 취소한다
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "CANCELLED" 상태로 변경한다
    Then 예약은 취소된다

  Scenario: 존재하지 않는 예약을 변경하는경우 실패한다.
    Given 예약이 존재하지 않는다
    When 관리자가 예약을 "CANCELLED" 상태로 변경한다
    Then 변경은 실패한다

  Scenario: 상태값을 입력하지 않은 경우 변경되지 않는다.
    Given 사용자가 예약을 했다
    When 관리자가 상태값을 입력하지 않고 변경한다
    Then 예약 상태는 변경되지 않는다

  Scenario: 상태값을 빈문자열로 변경하는 경우 기존 상태를 유지한다.
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "" 상태로 변경한다
    Then 예약 상태는 변경되지 않는다

  Scenario: 유효하지 않은 상태값으로 변경하는 실패한다.
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "UNKNOWN" 상태로 변경한다
    Then 변경은 실패한다

  Scenario: 예약 상태로 변경할때는 겹치는 예약이 없어야한다.
    Given 사용자가 예약을 했다
    And 관리자가 예약을 "CANCELLED" 상태로 변경한다
    And 다른 사용자가 동일 기간으로 예약을 했다
    When 관리자가 예약을 "CONFIRMED" 상태로 변경한다
    Then 변경은 실패한다