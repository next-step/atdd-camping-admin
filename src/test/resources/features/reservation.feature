Feature: 예약 상태 변경

  Background:
    Given 관리자가 로그인 한다.

  Scenario: 사용자의 예약을 관리자가 취소 한다.
    Given 사용자가 예약을 한다.
    When 관리자가 예약을 'CANCELLED' 한다.
    Then 예약이 'CANCELLED' 된다.

  Scenario: 사용자의 예약을 관리자가 체크인 한다.
    Given 사용자가 예약을 한다.
    When 관리자가 예약을 'CHECKED-IN' 한다.
    Then 예약이 'CHECKED-IN' 된다.
