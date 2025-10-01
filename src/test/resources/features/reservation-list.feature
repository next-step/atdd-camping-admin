Feature: 예약 목록을 조회한다.
  Background:
    Given 관리자가 로그인을 했다

  Scenario: 관리자가 예약 목록을 조회하면 성공 응답을 받는다.
    When 관리자가 예약 목록을 조회했다.
    Then 응답 상태코드는 200이다.
    And 응답 본문은 유효한 예약 목록이다.