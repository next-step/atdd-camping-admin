Feature: 상품 관리

  Background:
    Given 관리자 로그인이 되어 있다

  Scenario: 새로운 상품을 등록한다
    When 관리자가 이름이 '텐트', 타입이 'RENTAL', 가격이 100000인 상품을 등록하면
    Then 상품 등록이 성공한다
