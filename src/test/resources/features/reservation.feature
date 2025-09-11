Feature: 관리자의 예약 기능

  Background:
    Given 관리자가 로그인 한다.

  Scenario Outline: 관리자가 예약 상태를 <target> 으로 변경한다.
    Given 사용자가 예약을 한다.
    When 관리자가 예약을 '<target>' 한다.
    Then 예약이 '<target>' 된다.

  Examples:
    | target     |
    | CANCELLED  |
    | CHECKED-IN |

  Scenario: 관리자가 예약 상태를 이상한 값으로 변경한다.
    Given 사용자가 예약을 한다.
    When 관리자가 예약을 'PIKACHU' 한다.
    Then 요청이 400 코드로 실패 한다.
    And 'PIKACHU 예약 상태는 없습니다.' 메시지가 응답 한다.

  Scenario: 취소된 예약은 취소가 불가능하다.
    Given 사용자가 예약을 한다.
    And 관리자가 예약을 'CANCELLED' 한다.
    When 관리자가 예약을 'CANCELLED' 한다.
    Then 요청이 409 코드로 실패 한다.
    And '이미 취소 되었습니다.' 메시지가 응답 한다.
