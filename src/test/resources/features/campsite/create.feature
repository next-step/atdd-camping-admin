Feature: 캠핑장 생성
  Scenario: 관리자가 새 캠핑장을 등록한다
    When 관리자가 새 캠핑장을 등록한다
    Then 캠핑장이 등록된다

  Scenario: 중복된 사이트 번호로 캠핑장을 등록할 수 없다
    Given 사이트 번호 A-01 캠핑장이 있다
    When 관리자가 사이트 번호 A-01로 캠핑장을 등록한다
    Then 캠핑장 등록이 거부된다