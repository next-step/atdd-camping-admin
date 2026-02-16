Feature: 상품 관리
  캠핑장 관리자는 판매 및 대여 상품을 조회, 등록, 수정할 수 있다.

  Scenario: 상품 목록을 조회한다
    Given 다음 상품이 등록되어 있다
      | name | stockQuantity | price | productType |
      | 랜턴   | 20            | 30000 | RENTAL      |
      | 장작팩  | 50            | 10000 | SALE        |
    When 상품 목록을 조회 요청하면 (GET "/admin/products")
    Then 요청이 성공한다 (200)
    And 응답 목록의 크기는 2이다

  Scenario: 판매 상품을 등록한다
    When 다음 정보로 상품 등록 요청을 보내면 (POST "/admin/products")
      | name | stockQuantity | price | productType |
      | 모기향  | 100           | 3000  | SALE        |
    Then 리소스가 생성된다 (201)
    And 응답의 "name" 값은 "모기향"이다
    And 응답의 "productType" 값은 "SALE"이다
    And 응답의 "stockQuantity" 값은 100이다

  Scenario: 대여 상품을 등록한다
    When 다음 정보로 상품 등록 요청을 보내면 (POST "/admin/products")
      | name | stockQuantity | price | productType |
      | 텐트   | 10            | 50000 | RENTAL      |
    Then 리소스가 생성된다 (201)
    And 응답의 "name" 값은 "텐트"이다
    And 응답의 "productType" 값은 "RENTAL"이다

  Scenario: 상품 가격을 수정한다
    Given 상품명 "코펠 세트", 재고 15, 가격 20000, 유형 "RENTAL"인 상품이 등록되어 있다
    When 해당 상품에 다음 정보로 수정 요청을 보내면 (PUT "/admin/products/{id}")
      | price |
      | 35000 |
    Then 요청이 성공한다 (200)
    And 응답의 "price" 값은 35000이다

  Scenario: 상품 재고를 수정한다
    Given 상품명 "의자", 재고 25, 가격 15000, 유형 "RENTAL"인 상품이 등록되어 있다
    When 해당 상품에 다음 정보로 수정 요청을 보내면 (PUT "/admin/products/{id}")
      | stockQuantity |
      | 50            |
    Then 요청이 성공한다 (200)
    And 응답의 "stockQuantity" 값은 50이다
