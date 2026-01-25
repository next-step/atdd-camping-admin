Feature: Reservation Cancel
  Admin can cancel user's reservation

  Scenario: Admin cancels a reservation
    Given 사용자가 캠핑장 예약을 완료한 상태이다
    When 관리자가 해당 예약을 취소한다
    Then 예약이 성공적으로 취소된다
