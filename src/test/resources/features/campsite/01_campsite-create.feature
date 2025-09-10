Feature: 캠프사이트 생성 기능

  Scenario: 어드민이 캠프사이트를 생성한다.
    Given 어드민으로 로그인하였다
    When 캠프사이트를 생성한다
    Then 캠프사이트가 생성된다
