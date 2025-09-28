Feature: 예약 상태 변경

  Scenario: 확정된 예약을 체크인 후 체크아웃 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CHECKED_IN" 상태로 변경한다
    And 예약을 "CHECKED_OUT" 상태로 변경한다
    Then 예약 상태가 "CHECKED_OUT"으로 변경된다

  Scenario: 예약 정보 없이 상태 변경을 시도하면 오류가 발생한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약 상태 변경 정보를 제공하지 않고 요청한다
    Then 상태 변경이 실패한다

  Scenario: 존재하지 않는 예약의 상태를 변경하려고 하면 오류가 발생한다
    Given 존재하지 않는 예약 번호를 사용한다
    When 예약을 "CHECKED_OUT" 상태로 변경한다
    Then 상태 변경이 실패한다

  Scenario: 확정된 예약을 체크인 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CHECKED_IN" 상태로 변경한다
    Then 예약 상태가 "CHECKED_IN"으로 변경된다

  Scenario: 확정된 예약을 체크아웃 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CHECKED_OUT" 상태로 변경한다
    Then 예약 상태가 "CHECKED_OUT"으로 변경된다

  Scenario: 확정된 예약을 취소 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CANCELLED" 상태로 변경한다
    Then 예약 상태가 "CANCELLED"으로 변경된다

  Scenario: 확정된 예약을 다시 확정 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CONFIRMED" 상태로 변경한다
    Then 예약 상태가 "CONFIRMED"으로 변경된다

  Scenario: 확정된 예약을 체크인 상태로 변경한다
    Given "CONFIRMED" 상태인 예약이 있다
    When 예약을 "CHECKED_IN" 상태로 변경한다
    Then 예약 상태가 "CHECKED_IN"으로 변경된다

