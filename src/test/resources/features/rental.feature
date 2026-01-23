Feature: 대여 생성
  캠핑장 관리자가 장비를 대여해주는 기능

  Background:
    Given 관리자가 로그인했다

  Scenario: 대여 가능한 상품을 대여하면 성공한다
    Given 대여 가능한 상품이 존재한다
    When 해당 상품을 2개 대여하면
    Then 대여가 성공한다
    And 응답 상태코드는 201이다
