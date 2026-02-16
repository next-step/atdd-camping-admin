Feature: 매출 리포트
  캠핑장 관리자는 일별 및 기간별 매출 리포트를 조회할 수 있다.

  Background:
    Given 판매 상품 "장작팩"이 재고 50개, 가격 10000원으로 등록되어 있다
    And "장작팩" 3개 판매 기록이 존재한다

  Scenario: 일별 매출 리포트를 조회한다
    When 오늘 날짜로 일별 매출 리포트를 조회 요청하면 (GET "/admin/reports/revenue/daily?date={today}")
    Then 요청이 성공한다 (200)
    And 응답에 "totalRevenue" 필드가 존재한다

  Scenario: 기간별 매출 리포트를 조회한다
    When 최근 7일 기간으로 매출 리포트를 조회 요청하면 (GET "/admin/reports/revenue/range?from={7일전}&to={today}")
    Then 요청이 성공한다 (200)
    And 응답에 "totalRevenue" 필드가 존재한다

  Scenario: 기간별 매출 상세 내역을 조회한다
    When 최근 30일 기간으로 매출 상세 내역을 조회 요청하면 (GET "/admin/reports/revenue/range/entries?from={30일전}&to={today}")
    Then 요청이 성공한다 (200)
    And 응답 목록에 매출 항목이 포함되어 있다

  Scenario: 판매가 없는 날짜의 매출은 0이다
    When "2020-01-01" 날짜로 일별 매출 리포트를 조회 요청하면 (GET "/admin/reports/revenue/daily?date=2020-01-01")
    Then 요청이 성공한다 (200)
    And 응답의 "totalRevenue" 값은 0이다
