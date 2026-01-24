Feature: 판매 관리
  캠핑장 관리자가 상품을 판매하는 기능

  Background:
    Given 관리자가 로그인했다

  # === 판매 처리 ===
  Scenario: 판매 가능한 상품을 판매하면 성공한다
    Given 판매 가능한 상품이 존재한다
    When 해당 상품을 3개 판매하면
    Then 요청이 성공한다

  Scenario: 재고보다 많은 수량을 판매하면 실패한다
    Given 판매 가능한 상품이 존재한다
    When 해당 상품을 9999개 판매하면
    Then 요청이 실패한다

  Scenario: 존재하지 않는 상품을 판매하면 실패한다
    Given 존재하지 않는 상품 ID를 사용한다
    When 해당 상품을 1개 판매하면
    Then 요청이 실패한다

  # === 판매 목록 조회 ===
  Scenario: 판매 목록을 조회하면 성공한다
    When 판매 목록을 조회하면
    Then 목록이 조회된다
