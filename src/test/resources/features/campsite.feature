Feature: 캠프사이트 관리

  Background:
    Given 관리자가 로그인했다

  Scenario: 관리자가 캠프사이트 목록을 조회하면 성공한다
    When 관리자가 캠프사이트 목록을 조회한다
    Then 캠프사이트 목록이 반환된다
    And 캠프사이트 정보에는 사이트 번호와 최대 인원이 포함된다

  Scenario: 관리자가 새 캠프사이트를 생성하면 성공한다
    When 관리자가 다음 정보로 캠프사이트를 생성한다:
      | siteNumber | description      | maxPeople |
      | TEST-01    | 테스트 캠프사이트  | 4         |
    Then 응답 상태코드는 201이다
    And 생성된 캠프사이트 정보가 반환된다
    And 캠프사이트 번호는 "TEST-01"이다
    And 최대 인원은 4이다

  Scenario: 관리자가 사이트 번호 없이 캠프사이트를 생성하면 실패한다
    When 관리자가 다음 정보로 캠프사이트를 생성한다:
      | description      | maxPeople |
      | 호수 근처 자리   | 6         |
    Then 응답 상태코드는 500이다

  Scenario: 관리자가 최대 인원 없이 캠프사이트를 생성하면 성공한다
    When 관리자가 다음 정보로 캠프사이트를 생성한다:
      | siteNumber | description      |
      | TEST-02    | 산책로 근처 자리  |
    Then 응답 상태코드는 201이다
    And 생성된 캠프사이트의 최대 인원은 null이다
