Feature: 상품을 관리자가 관리한다

  Background:
    Given 관리자가 로그인했다

  Scenario: 관리자가 상품 목록을 조회하면 성공한다
    When 관리자가 상품 목록을 조회한다
    Then 상품 목록이 반환된다
    And 상품 정보에는 이름과 가격 정보가 포함된다

  Scenario: 관리자가 새 상품을 생성하면 성공한다
    When 관리자가 다음 정보로 상품을 생성한다:
      | name            | price | stockQuantity | productType |
      | 테스트 캠핑의자  | 50000 | 10           | RENTAL      |
    Then 응답 상태코드는 201이다
    And 생성된 상품 정보가 반환된다
    And 상품 이름은 "테스트 캠핑의자"이다
    And 상품 가격은 50000이다

  Scenario: 관리자가 필수 정보 없이 상품을 생성하면 실패한다
    When 관리자가 다음 정보로 상품을 생성한다:
      | price | stockQuantity | productType |
      | 50000 | 10           | RENTAL      |
    Then 응답 상태코드는 400이다

  Scenario: 관리자가 잘못된 상품 타입으로 상품을 생성하면 기본값이 적용된다
    When 관리자가 다음 정보로 상품을 생성한다:
      | name           | price  | stockQuantity | productType |
      | 테스트 캠핑텐트 | 100000 | 5            | INVALID     |
    Then 응답 상태코드는 201이다
    And 생성된 상품의 타입은 "SALE"이다

  Scenario: 관리자가 음수 가격으로 상품을 생성하면 실패한다
    When 관리자가 다음 정보로 상품을 생성한다:
      | name          | price | stockQuantity | productType |
      | 테스트 상품   | -1000 | 10           | SALE        |
    Then 응답 상태코드는 400이다

  Scenario: 관리자가 음수 재고로 상품을 생성하면 실패한다
    When 관리자가 다음 정보로 상품을 생성한다:
      | name          | price | stockQuantity | productType |
      | 테스트 상품   | 10000 | -5           | SALE        |
    Then 응답 상태코드는 400이다

  Scenario: 권한 없는 사용자가 상품을 생성하려 하면 실패한다
    When 권한 없는 사용자가 다음 정보로 상품을 생성한다:
      | name          | price | stockQuantity | productType |
      | 테스트 상품   | 10000 | 10           | SALE        |
    Then 응답 상태코드는 401이다

  Scenario: 권한 없는 사용자가 상품 목록을 조회하려 하면 실패한다
    When 권한 없는 사용자가 상품 목록을 조회한다
    Then 응답 상태코드는 401이다
