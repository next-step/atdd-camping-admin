Feature: 상품 관리

  Scenario: 새로운 상품을 등록한다
    When 관리자가 새로운 상품을 등록한다
    Then 상품 등록에 성공한다

  Scenario: 모든 상품 목록을 조회한다
    Given 상품이 3개 등록되어 있다
    When 관리자가 상품 목록을 조회한다
    Then 상품 목록 조회에 성공한다

  Scenario: 상품 정보를 수정한다
    Given 상품이 등록되어 있다
    When 관리자가 상품 정보를 수정한다
    Then 상품 수정에 성공한다

  Scenario: 필수 정보 없이 상품 등록을 시도하면 실패한다
    When 관리자가 이름 없이 상품을 등록한다
    Then 상품 등록에 실패한다
