Feature: 캠프사이트를 관리자가 관리한다

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
    Then 응답 상태코드는 400이다

  Scenario: 관리자가 최대 인원 없이 캠프사이트를 생성하면 성공한다
    When 관리자가 다음 정보로 캠프사이트를 생성한다:
      | siteNumber | description      |
      | TEST-02    | 산책로 근처 자리  |
    Then 응답 상태코드는 201이다
    And 생성된 캠프사이트의 최대 인원은 null이다

  Scenario: 관리자가 중복된 사이트 번호로 캠프사이트를 생성하면 실패한다
    Given 관리자가 다음 정보로 캠프사이트를 생성했다:
      | siteNumber | description | maxPeople |
      | TEST-DUPLICATE   | 첫 번째 사이트 | 4       |
    When 관리자가 다음 정보로 캠프사이트를 생성한다:
      | siteNumber | description | maxPeople |
      | TEST-DUPLICATE   | 두 번째 사이트 | 6       |
    Then 응답 상태코드는 409이다

  Scenario: 관리자가 음수 최대 인원으로 캠프사이트를 생성하면 실패한다
    When 관리자가 다음 정보로 캠프사이트를 생성한다:
      | siteNumber | description      | maxPeople |
      | TEST-03    | 테스트 사이트     | -1        |
    Then 응답 상태코드는 400이다

  Scenario: 권한 없는 사용자가 캠프사이트를 생성하려 하면 실패한다
    When 권한 없는 사용자가 다음 정보로 캠프사이트를 생성한다:
      | siteNumber | description | maxPeople |
      | UNAUTHORIZED | 권한 없음 | 4       |
    Then 응답 상태코드는 401이다

  Scenario: 권한 없는 사용자가 캠프사이트 목록을 조회하려 하면 실패한다
    When 권한 없는 사용자가 캠프사이트 목록을 조회한다
    Then 응답 상태코드는 401이다
