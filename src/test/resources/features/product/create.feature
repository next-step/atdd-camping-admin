Feature: 상품 생성
  Scenario: 관리자가 새 상품을 등록한다
    Given 관리자가 로그인되어 있다
    When 관리자가 새 상품을 등록한다
    Then 상품이 등록된다

  Scenario: 필수 정보 없이 상품을 등록할 수 없다
    Given 관리자가 로그인되어 있다
    When 관리자가 이름 없이 상품을 등록한다
    Then 상품 등록이 거부된다