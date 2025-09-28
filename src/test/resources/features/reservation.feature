Feature: 예약 상태를 관리자가 수행한다
  Background:
    Given 관리자가 로그인한다
    And 사용자가 예약했다

  Scenario: 관리자가 예약을 대기 상태로 바꿀 수 있다
    When 관리자가 예약을 "WAITING" 상태로 바꾼다
    Then 예약은 "WAITING" 상태가 된다

  Scenario: 예약을 관리자가 취소한 뒤 다시 예약할 수 있다
    When 관리자가 예약을 취소했다
    Then 예약은 취소 상태다
    And 재예약에 성공한다

  Scenario: 이미 체크아웃된 예약을 다시 체크아웃하려 하면 상태가 그대로 유지된다
    When 관리자가 예약을 체크아웃 상태로 바꾼다
    And 관리자가 체크아웃된 예약의 상태를 동일한 상태로 바꾼다
    Then 예약의 상태는 그대로 유지된다

  Scenario: 예약 상태를 허용되지 않은 문자열로 변경할 수 없다
    When 예약의 상태를 "DONE"으로 변경한다
    Then 예약의 상태 변경에 실패한다