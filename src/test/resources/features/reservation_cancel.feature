# src/test/resources/features/reservation_cancel.feature
Feature: 관리자의 예약 취소 기능

  Scenario: 사용자가 예약한 건을 관리자가 취소하면 성공한다
    Given 사용자가 캠핑장 예약을 했다
    When 관리자가 해당 예약을 취소하면
    Then 예약이 성공적으로 취소된다