Feature: 관리자 예약 관리 기능

  Scenario: 사용자가 예약한 건을 관리자가 취소할 수 있다.
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "CANCELLED" 상태로 변경한다
    Then 예약은 "CANCELLED" 상태다

  Scenario: 사용자의 예약을 관리자가 체크인 상태로 변경한다.
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "CHECKED_IN" 상태로 변경한다
    Then 변경 요청이 성공한다
    And 예약은 "CHECKED_IN" 상태다

  Scenario: 취소 상태인 사용자 예약은 체크인 상태로 변경할 수 없다.
    Given 사용자가 예약을 했다
    And 관리자가 예약을 "CANCELLED" 상태로 변경한다
    When 관리자가 예약을 "CHECKED_IN" 상태로 변경한다
    Then 예약 상태 변경이 실패한다
    And 예약은 "CANCELLED" 상태다

  Scenario: 사용자 예약 상태 변경 내용이 없으면 변경이 실패한다.
    Given 사용자가 예약을 했다
    When 관리자가 변경 내용 없이 사용자 예약 상태 변경을 시도한다
    Then 잘못된 요청 형식으로 인해 예약 상태 변경이 실패한다
    And 응답에 기존 사용자 예약 정보가 포함된다
