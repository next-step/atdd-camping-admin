Feature: 대여를 관리자가 관리한다

  Background:
    Given 관리자가 로그인했다

  Scenario: 관리자가 대여 목록을 조회하면 성공한다
    When 관리자가 대여 목록을 조회한다
    Then 대여 목록이 반환된다
    And 대여 정보에는 상품과 수량 정보가 포함된다

  Scenario: 관리자가 예약과 함께 대여를 생성하면 성공한다
    When 관리자가 다음 정보로 대여를 생성한다:
      | productId | quantity | reservationId |
      | 1         | 2        | 1            |
    Then 응답 상태코드는 201이다
    And 생성된 대여 정보가 반환된다
    And 대여 수량은 2이다

  Scenario: 관리자가 예약 없이 대여를 생성하면 성공한다
    When 관리자가 다음 정보로 대여를 생성한다:
      | productId | quantity |
      | 3         | 1        |
    Then 응답 상태코드는 201이다
    And 생성된 대여 정보가 반환된다
    And 대여의 예약 ID는 null이다

  Scenario: 관리자가 존재하지 않는 상품으로 대여를 생성하면 실패한다
    When 관리자가 다음 정보로 대여를 생성한다:
      | productId | quantity |
      | 999       | 1        |
    Then 응답 상태코드는 404이다
