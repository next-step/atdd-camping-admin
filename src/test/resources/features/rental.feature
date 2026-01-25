Feature: 대여 관리
  캠핑장 관리자가 장비를 대여/반납하는 기능

  Background:
    Given 관리자가 로그인했다

  # === 대여 생성 ===
  Scenario: 대여 가능한 상품을 대여하면 성공한다
    Given 대여 가능한 상품이 존재한다
    When 해당 상품을 2개 대여하면
    Then 요청이 성공한다

  Scenario: 판매 전용 상품을 대여하면 실패한다
    Given 판매 가능한 상품이 존재한다
    When 해당 상품을 1개 대여하면
    Then 요청이 실패한다

  Scenario: 재고보다 많은 수량을 대여하면 실패한다
    Given 대여 가능한 상품이 존재한다
    When 해당 상품을 9999개 대여하면
    Then 요청이 실패한다

  Scenario: 존재하지 않는 상품을 대여하면 실패한다
    Given 존재하지 않는 상품 ID를 사용한다
    When 해당 상품을 1개 대여하면
    Then 요청이 실패한다

  # === 반납 ===
  Scenario: 대여한 상품을 반납하면 성공한다
    Given 반납되지 않은 대여 기록이 존재한다
    When 해당 대여 기록을 반납하면
    Then 요청이 성공한다

  Scenario: 이미 반납된 상품을 다시 반납하면 실패한다
    Given 이미 반납된 대여 기록이 존재한다
    When 해당 대여 기록을 반납하면
    Then 요청이 실패한다

  Scenario: 존재하지 않는 대여 기록을 반납하면 실패한다
    Given 존재하지 않는 대여 기록 ID를 사용한다
    When 해당 대여 기록을 반납하면
    Then 요청이 실패한다
