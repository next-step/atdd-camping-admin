Feature: 대여 생성

  Scenario: 상품이 등록되어 있고 렌탈 상품이라면, 관리자는 특정 상품의 수량 만큼 대여 기록을 작성할 수 있다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 1 |
      | quantity      | 2 |
    Then 대여 기록이 생성된다.
    And 상품의 수량이 대여 기록의 수량 만큼 줄어든다.

  Scenario: 상품이 렌탈 상품이 아니라면, 관리자는 대여 기록을 작성할 수 없다,
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 2 |
      | quantity      | 1 |
    Then 대여에 실패한다.
    And 대여 기록이 생성되지 않는다.
      | productId | 2 |

  Scenario: 존재하지 않는 상품에 대해, 관리자는 대여 기록을 작성할 수 없다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 100 |
      | quantity      | 1 |
    Then 대여에 실패한다.
    And 대여 기록이 생성되지 않는다.
      | productId | 2 |