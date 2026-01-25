Feature: 상품 수정
  Scenario: 관리자가 상품 정보를 수정한다
    Given 등록된 상품이 있다
    When 관리자가 상품 정보를 수정한다
    Then 상품 정보가 수정된다

  Scenario: 존재하지 않는 상품을 수정할 수 없다
    Given 관리자가 로그인되어 있다
    When 관리자가 존재하지 않는 상품을 수정한다
    Then 상품 수정이 거부된다