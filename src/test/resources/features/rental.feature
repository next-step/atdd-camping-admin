Feature: 대여 관리

  Scenario: 관리자가 모든 대여 기록을 조회할 수 있다.
    Given 대여 기록이 존재한다.
    When 관리자가 대여 기록 목록을 조회한다.
    Then 대여 기록 목록이 조회된다.

  Scenario: 상품이 등록되어 있고 렌탈 상품이라면, 관리자는 특정 상품의 수량 만큼 대여 기록을 작성할 수 있다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 1 |
      | quantity      | 2 |
    Then 대여 기록이 생성된다.
    And 상품의 수량이 대여 기록의 수량 만큼 줄어든다.

  Scenario: 관리자가 대여 반납 처리를 할 수 있다.
    Given 반납되지 않은 대여 기록이 존재한다.
    When 관리자가 대여 반납 처리를 한다.
    Then 대여가 반납 처리된다.

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

  Scenario: 워크인 고객도 대여 기록을 작성할 수 있다.
    When 관리자는 워크인 고객의 대여 기록을 작성한다.
      | productId | 1 |
      | quantity  | 1 |
    Then 대여 기록이 생성된다.
    And 예약 정보가 없는 대여 기록이 생성된다.

  Scenario: 이미 반납된 대여 기록을 다시 반납하려고 하면 실패한다.
    Given 이미 반납된 대여 기록이 존재한다.
    When 관리자가 이미 반납된 대여 기록을 반납 처리한다.
    Then 대여 반납이 실패한다.

  Scenario: 존재하지 않는 대여 기록을 반납하려고 하면 실패한다.
    Given 존재하지 않는 대여 기록이 있다.
    When 관리자가 존재하지 않는 대여 기록을 반납 처리한다.
    Then 대여 반납이 실패한다.

  Scenario: 음수 수량으로 대여 기록을 작성하려고 하면 실패한다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 1 |
      | quantity      | -1 |
    Then 유효하지 않은 수량으로 인해 대여에 실패한다.

  Scenario: 0 수량으로 대여 기록을 작성하려고 하면 실패한다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 1 |
      | productId     | 1 |
      | quantity      | 0 |
    Then 유효하지 않은 수량으로 인해 대여에 실패한다.

  Scenario: 존재하지 않는 예약으로 대여 기록을 작성하려고 하면 실패한다.
    When 관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.
      | reservationId | 999 |
      | productId     | 1 |
      | quantity      | 1 |
    Then 대여에 실패한다.