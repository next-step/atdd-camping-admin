Feature: 대여 반납
  Scenario: 대여한 장비를 반납한다
    Given 대여 중인 장비가 있다
    When 관리자가 해당 장비 반납 처리한다
    Then 반납이 완료된다
    And 상품 재고가 복구된다

  Scenario: 이미 반납된 장비는 다시 반납할 수 없다
    Given 이미 반납된 대여 기록이 있다
    When 관리자가 해당 장비 반납 처리한다
    Then 반납이 거부된다