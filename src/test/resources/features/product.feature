Feature: 상품 관리
  캠핑장 관리자가 상품을 관리하는 기능

  Background:
    Given 관리자가 로그인했다

  # === 상품 생성 ===
  Scenario: 상품을 생성하면 성공한다
    When 상품을 생성하면
    Then 요청이 성공한다

  Scenario: 이름 없이 상품을 생성하면 실패한다
    When 이름 없이 상품을 생성하면
    Then 요청이 실패한다

  # === 상품 목록 조회 ===
  Scenario: 상품 목록을 조회하면 성공한다
    When 상품 목록을 조회하면
    Then 목록이 조회된다

  # === 상품 수정 ===
  Scenario: 상품 정보를 수정하면 성공한다
    Given 수정할 상품이 존재한다
    When 해당 상품 정보를 수정하면
    Then 요청이 성공한다

  Scenario: 존재하지 않는 상품을 수정하면 실패한다
    Given 존재하지 않는 상품 ID를 사용한다
    When 해당 상품 정보를 수정하면
    Then 리소스를 찾을 수 없다
