Feature: 상품 관리를 관리자가 수행한다
  Scenario: 관리자가 상품의 판매 방식을 변경할 수 있다
    Given 관리자가 상품 판매를 시작했다
    When 관리자가 판매방식을 "RENTAL"로 변경했다
    Then 상품을 대여할 수 있게 된다