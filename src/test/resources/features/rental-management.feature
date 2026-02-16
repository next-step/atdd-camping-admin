Feature: 대여 관리
  캠핑장 관리자는 장비 대여를 조회, 등록하고 반납 처리할 수 있다.

  Background:
    Given 대여 상품 "랜턴"이 재고 20개로 등록되어 있다
    And 캠프사이트가 등록되어 있다
    And "CONFIRMED" 상태의 예약이 존재한다

  Scenario: 대여 목록을 조회한다
    Given "랜턴" 2개의 대여 기록이 존재한다
    When 대여 목록을 조회 요청하면 (GET "/admin/rentals")
    Then 요청이 성공한다 (200)
    And 응답 목록에 대여 기록이 포함되어 있다

  Scenario: 예약 고객에게 장비를 대여한다
    When 예약 ID와 상품 "랜턴" 2개로 대여 등록 요청을 보내면 (POST "/admin/rentals")
    Then 리소스가 생성된다 (201)
    And 응답의 "quantity" 값은 2이다
    And 응답의 "isReturned" 값은 false이다
    And 상품 "랜턴"의 재고는 18이다

  Scenario: 워크인 고객에게 장비를 대여한다
    When 예약 없이 상품 "랜턴" 1개로 대여 등록 요청을 보내면 (POST "/admin/rentals")
    Then 리소스가 생성된다 (201)
    And 응답의 "reservationId" 값은 null이다
    And 응답의 "isReturned" 값은 false이다

  Scenario: 대여 장비를 반납 처리한다
    Given "랜턴" 3개의 대여 기록이 존재한다
    When 해당 대여 기록에 반납 요청을 보내면 (PATCH "/admin/rentals/{id}/return")
    Then 요청이 성공한다 (200)
    And 응답의 "isReturned" 값은 true이다
    And 상품 "랜턴"의 재고는 20이다
