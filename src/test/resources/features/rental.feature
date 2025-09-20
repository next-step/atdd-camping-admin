Feature: 대여 생성
  Scenario: 예약이 있는 고객의 대여 생성
    Given 사용자가 예약을 했다.
    And 제품의 재고가 있다.
    When 사용자가 제품을 대여한다
    Then 대여에 성공했다
    And 대여 ID가 생성된다
    And 대여된 제품의 재고가 감소했다
    And 대여 상태가 '대여중'이다

  Scenario: 존재하지 않는 제품으로 대여 요청
    Given 사용자가 예약을 했다.
    When 사용자가 존재하지 않는 제품을 대여 요청한다
    Then 대여 요청이 실패한다
    And 에러 메시지가 "Cannot find product with id"를 포함한다

  Scenario: 대여용이 아닌 제품으로 대여 요청
    Given 사용자가 예약을 했다.
    And 판매용 제품이 있다.
    When 사용자가 판매용 제품을 대여 요청한다
    Then 대여 요청이 실패한다
    And 에러 메시지가 "Product is not a rental item"를 포함한다

  Scenario: 재고가 부족한 제품으로 대여 요청
    Given 사용자가 예약을 했다.
    And 재고가 부족한 대여 제품이 있다.
    When 사용자가 재고보다 많은 수량을 대여 요청한다
    Then 대여 요청이 실패한다
    And 에러 메시지가 "Not enough stock for product"를 포함한다

  Scenario: 존재하지 않는 예약으로 대여 요청
    Given 제품의 재고가 있다.
    When 사용자가 존재하지 않는 예약으로 제품을 대여 요청한다
    Then 대여 요청이 실패한다
    And 에러 메시지가 "Cannot find reservation with id"를 포함한다

  Scenario: 예약 없이 대여 요청 (워크인 고객)
    Given 제품의 재고가 있다.
    When 사용자가 예약 없이 제품을 대여 요청한다
    Then 대여에 성공했다
    And 대여 ID가 생성된다
    And 예약 ID가 null이다
    And 대여된 제품의 재고가 감소했다
    And 대여 상태가 '대여중'이다