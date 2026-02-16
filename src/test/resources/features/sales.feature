Feature: 판매 처리
  캠핑장에서 상품을 판매하고 판매 내역을 조회할 수 있다.

  Background:
    Given 판매 상품 "장작팩"이 재고 50개, 가격 10000원으로 등록되어 있다

  Scenario: 상품을 판매한다
    When "장작팩" 3개를 판매 요청하면 (POST "/api/sales")
    Then 요청이 성공한다 (200)
    And 상품 "장작팩"의 재고는 47이다

  Scenario: 여러 상품을 한번에 판매한다
    Given 판매 상품 "생수(2L)"가 재고 100개, 가격 2000원으로 등록되어 있다
    When 다음 상품들을 판매 요청하면 (POST "/api/sales")
      | productName | quantity |
      | 장작팩         | 2        |
      | 생수(2L)      | 5        |
    Then 요청이 성공한다 (200)
    And 상품 "장작팩"의 재고는 48이다
    And 상품 "생수(2L)"의 재고는 95이다

  Scenario: 최근 판매 내역을 조회한다
    Given "장작팩" 3개 판매 기록이 존재한다
    When 판매 내역을 조회 요청하면 (GET "/api/sales")
    Then 요청이 성공한다 (200)
    And 응답 목록의 크기는 최대 10이다
