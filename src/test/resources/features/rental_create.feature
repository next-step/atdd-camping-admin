Feature: 관리자의 대여 생성 기능

  Background:
    Given 관리자가 로그인되어 있다

  # Happy Path
  Scenario: 예약 고객에게 장비를 대여하면 성공한다
    Given 대여 가능한 상품 "랜턴"이 존재한다
    And 고객 "홍길동"의 예약이 존재한다
    When 관리자가 해당 고객에게 "랜턴" 2개를 대여하면
    Then 대여가 성공적으로 생성된다

  # Edge Case: 워크인 대여
  Scenario: 예약 없이 워크인 고객에게 장비를 대여하면 성공한다
    Given 대여 가능한 상품 "랜턴"이 존재한다
    When 관리자가 워크인 고객에게 "랜턴" 1개를 대여하면
    Then 대여가 성공적으로 생성된다

  # Exception: 존재하지 않는 상품
  Scenario: 존재하지 않는 상품으로 대여하면 실패한다
    When 관리자가 존재하지 않는 상품으로 대여하면
    Then 대여가 실패한다

  # Exception: SALE 타입 상품
  Scenario: 판매 전용 상품으로 대여하면 실패한다
    Given 판매 전용 상품 "장작팩"이 존재한다
    When 관리자가 "장작팩"으로 대여하면
    Then 대여가 실패한다

  # Exception: 재고 부족
  Scenario: 재고보다 많은 수량을 대여하면 실패한다
    Given 대여 가능한 상품 "테이블"이 존재한다
    When 관리자가 "테이블" 100개를 대여하면
    Then 대여가 실패한다

  # Exception: 존재하지 않는 예약
  Scenario: 존재하지 않는 예약으로 대여하면 실패한다
    Given 대여 가능한 상품 "랜턴"이 존재한다
    When 관리자가 존재하지 않는 예약으로 대여하면
    Then 대여가 실패한다
