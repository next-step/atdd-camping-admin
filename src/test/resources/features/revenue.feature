Feature: 매출 통계
  캠핑장 관리자가 매출 통계를 조회하는 기능

  Background:
    Given 관리자가 로그인했다

  # === 매출 통계 조회 ===
  Scenario: 일별 매출을 조회하면 성공한다
    When 일별 매출을 조회하면
    Then 요청이 성공한다

  Scenario: 월별 매출을 조회하면 성공한다
    When 월별 매출을 조회하면
    Then 요청이 성공한다

  Scenario: 기간별 매출을 조회하면 성공한다
    When 기간별 매출을 조회하면
    Then 요청이 성공한다
