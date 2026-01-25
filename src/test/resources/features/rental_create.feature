Feature: 관리자의 대여 생성 기능

  Background:
    Given 대여 가능한 상품이 존재한다

  Scenario: 예약 고객에게 장비를 대여하면 성공한다
    Given 고객이 캠핑장 예약을 했다
    When 관리자가 해당 고객에게 장비를 대여하면
    Then 대여가 성공적으로 생성된다
