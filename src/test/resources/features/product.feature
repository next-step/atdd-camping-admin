Feature: 상품 관리

  Scenario: 관리자가 상품을 성공적으로 등록한다
    When 관리자가 이름 "장작", 재고 50개, 가격 10000원, 타입 "SALE"인 상품을 등록하면
    Then 상품이 성공적으로 등록된다
    And 등록된 상품의 이름은 "장작", 재고는 50개, 가격은 10000원, 타입은 "SALE"이어야 한다

  Scenario: 상품 등록 시 타입이 누락되면 기본값인 SALE로 등록된다
    When 관리자가 이름 "기본상품", 재고 10개, 가격 5000원, 타입 ""인 상품을 등록하면
    Then 상품이 성공적으로 등록된다
    And 등록된 상품의 이름은 "기본상품", 재고는 10개, 가격은 5000원, 타입은 "SALE"이어야 한다

  Scenario Outline: 필수 값이 유효하지 않으면 상품 등록에 실패한다
    When 관리자가 이름 <name>, 재고 <stockQuantity>개, 가격 <price>원, 타입 <productType>인 상품을 등록하면
    Then 상품 등록이 실패한다

    Examples:
      | name    | stockQuantity | price | productType |
      | ""      | 10            | 10000 | "SALE"      |
      | "장작"    | -1            | 10000 | "SALE"      |
      | "장작"    | 10            | -100  | "SALE"      |
