Feature: 매출 리포트
  캠핑장 관리자는 일별 및 기간별 매출 리포트를 조회할 수 있다.

  Background:
    Given 판매 상품과 판매 기록이 존재한다

  Scenario: 일별 매출 리포트를 조회한다
    When 오늘 날짜로 일별 매출 리포트를 조회한다 (GET "/admin/reports/revenue/daily")
    Then 조회에 성공한다 (200)
    And 매출 합계가 반환된다

  Scenario: 기간별 매출 리포트를 조회한다
    When 최근 7일 기간으로 매출 리포트를 조회한다 (GET "/admin/reports/revenue/range")
    Then 조회에 성공한다 (200)
    And 매출 합계가 반환된다

  Scenario: 기간별 매출 상세 내역을 조회한다
    When 최근 30일 기간으로 매출 상세 내역을 조회한다 (GET "/admin/reports/revenue/range/entries")
    Then 조회에 성공한다 (200)
    And 매출 내역이 반환된다

  Scenario: 판매가 없는 날짜의 매출은 0이다
    When 판매 기록이 없는 날짜의 일별 매출 리포트를 조회한다 (GET "/admin/reports/revenue/daily")
    Then 조회에 성공한다 (200)
    And 매출 합계는 0이다
