Feature: 관리자가 예약 확인코드를 변경한다
  Scenario: 관리자가 예약 확인코드를 변경하면 성공하고 응답에 반영된다
    Given 사용자가 예약을 했다
    When 관리자가 확인코드를 "ABC123" 으로 변경한다
    Then 예약의 확인코드는 "ABC123" 이다

  Scenario: 관리자가 존재하지 않는 예약ID를 사용해서 확인코드를 변경하려고 하면 실패한다
    When 관리자가 존재하지 않는 예약ID를 사용해서 확인코드를 "ZZZZZZ" 로 변경하려고 한다
    Then 예약 확인코드를 변경하면 실패한다

  Scenario Outline: 관리자가 잘못된 확인코드로 변경하면 실패한다
    Given 사용자가 예약을 했다
    When 관리자가 잘못된 확인코드 <code> 로 변경한다
    Then 예약 확인코드를 변경하면 실패한다

    Examples:
      | code      |
      | ""        |
      | "ABC"     |
      | "ABCDEFG" |
