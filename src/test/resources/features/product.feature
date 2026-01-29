Feature: 관리자의 상품 관리

  Background:
    Given 관리자가 로그인되어 있다

  # ===== Happy Path =====
  Scenario: 대여 상품을 등록할 수 있다
    When 관리자가 대여 상품을 등록한다
    Then 상품이 등록된다

  Scenario: 판매 상품을 등록할 수 있다
    When 관리자가 판매 상품을 등록한다
    Then 상품이 등록된다

  Scenario: 상품 정보를 수정할 수 있다
    Given 등록된 상품이 있다
    When 관리자가 상품 정보를 수정한다
    Then 상품이 수정된다

  Scenario: 상품 목록을 조회할 수 있다
    When 관리자가 상품 목록을 조회한다
    Then 상품 목록이 반환된다

  # ===== 인증 실패 =====
  Scenario: 로그인하지 않으면 상품을 등록할 수 없다
    When 관리자 권한 없이 상품을 등록한다
    Then 인증 오류가 발생한다

  Scenario: 로그인하지 않으면 상품을 수정할 수 없다
    When 관리자 권한 없이 상품을 수정한다
    Then 인증 오류가 발생한다

  Scenario: 로그인하지 않으면 상품 목록을 조회할 수 없다
    When 관리자 권한 없이 상품 목록을 조회한다
    Then 인증 오류가 발생한다

  # ===== 수정 실패 =====
  Scenario: 존재하지 않는 상품은 수정할 수 없다
    When 관리자가 존재하지 않는 상품을 수정한다
    Then 수정이 실패한다