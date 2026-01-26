Feature: 판매 처리
  Scenario: 상품을 판매한다
    Given 판매 가능한 상품이 있다
    When 관리자가 해당 상품을 판매한다
    Then 판매가 완료된다
    And 판매 상품 재고가 감소한다

  Scenario: 재고보다 많이 판매할 수 없다
    Given 재고가 5인 판매 상품이 있다
    When 관리자가 해당 상품을 10개 판매한다
    Then 판매가 거부된다