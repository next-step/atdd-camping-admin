Feature: 대여 생성
  Scenario: 예약이 있는 고객의 대여 생성
    Given 예약이 있다
    And 제품의 재고가 10개 있다
    When 사용자가 제품 2개를 대여한다
    Then 대여에 성공했다
    And 대여 ID가 생성된다
    And 제품 재고가 8개로 감소된다
    And 대여 상태가 '대여중'이다