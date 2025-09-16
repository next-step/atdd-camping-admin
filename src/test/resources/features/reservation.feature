Feature: 관리자의 예약 기능

  Background:
    Given 사용자가 예약을 한다.

  Scenario Outline: 관리자가 예약 상태를 <target> 으로 변경한다.
    When 관리자가 예약을 '<target>' 한다.
    Then 예약 상태 변경이 성공 된다.
    And 예약이 '<target>' 된다.

  Examples:
    | target     |
    | CANCELLED  |
    | CHECKED_IN |

  Scenario: 예약 상태는 이상한 값으로 변경 불가능하다.
    When 관리자가 예약을 'PIKACHU' 한다.
    Then 존재하지 않는 예약 상태 변경이 실패 된다.
    And 'PIKACHU 예약 상태는 없습니다.' 메시지가 응답 된다.

  Scenario: 취소된 예약은 취소가 불가능하다.
    Given 관리자가 예약을 'CANCELLED' 한다.
    When 관리자가 예약을 'CANCELLED' 한다.
    Then 변경할 수 없는 예약 상태 변경이 실패 된다.
    And '이미 취소된 예약 입니다.' 메시지가 응답 된다.

  Scenario: 취소된 예약은 다시 예약할 수 있다.
    Given 관리자가 예약을 'CANCELLED' 한다.
    When 사용자가 예약을 한다.
    Then 예약이 'CONFIRMED' 된다.

  Scenario: 관리자는 예약 목록을 조회할 수 있다.
    When 관리자가 예약 목록을 조회 한다.
    Then 예약 목록 조회가 성공 된다.
    And 예약 목록이 12개 조회 된다.

  Scenario: 관리자는 빈 예약 목록을 조회할 수 있다.
    Given 예약 데이터를 모두 삭제 한다.
    When 관리자가 예약 목록을 조회 한다.
    Then 예약 목록 조회가 성공 된다.
    And 예약 목록이 0개 조회 된다.
