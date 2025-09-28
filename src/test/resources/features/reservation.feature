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

  Scenario: 사용자 예약을 변경할 상태를 null값으로 지정하면 예약 상태가 변경되지 않는다
    Given 사용자가 예약을 했다
    When 관리자가 예약 상태를 null 값으로 변경을 시도한다
    Then 잘못된 요청 형식으로 인해 예약 상태 변경이 실패한다
    And 예약은 "CONFIRMED" 상태다

  Scenario: 사용자 예약을 변경할 상태를 빈 문자열로 지정하면 예약 상태가 변경되지 않는다
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "" 상태로 변경한다
    Then 잘못된 요청 형식으로 인해 예약 상태 변경이 실패한다
    And 예약은 "CONFIRMED" 상태다

  Scenario: 체크인 상태가 아닌 사용자 예약은 체크아웃 상태로 변경할 수 없다
    Given 사용자가 예약을 했다
    When 관리자가 예약을 "CHECKED_OUT" 상태로 변경한다
    Then 예약 상태 변경이 실패한다
    And 예약은 "CONFIRMED" 상태다

  # --- 전체 예약 목록 조회 ---

  Scenario: 관리자가 사용자들의 예약 목록을 조회한다
    Given 사용자가 예약을 했다
    When 관리자가 전체 예약을 조회한다
    Then 조회된 예약 목록에는 1개 이상의 예약이 포함되어 있다
