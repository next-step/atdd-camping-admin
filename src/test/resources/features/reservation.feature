Feature: 예약 상태 변경

  Background:
    Given 관리자가 로그인 한다.

  Scenario: 사용자의 예약을 관리자가 취소한다.
    Given 사용자가 예약을 한다.
    When 관리자가 예약을 취소 한다.
    Then 예약이 취소 된다.
