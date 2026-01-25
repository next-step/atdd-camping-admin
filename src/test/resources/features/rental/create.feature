Feature: 대여 생성
  Scenario: 고객에게 장비를 대여한다
    Given 대여 가능한 상품이 있다
    When 관리자가 해당 상품을 대여한다
    Then 대여가 생성된다
    And 대여 상품 재고가 감소한다

  Scenario: 재고가 부족하면 대여할 수 없다
    Given 재고가 0인 대여 상품이 있다
    When 관리자가 해당 상품을 대여한다
    Then 대여가 거부된다