Feature: 캠핑 사이트 등록
  Background:
    Given 관리자가 로그인했다
  Scenario: 새로운 캠핑 사이트를 등록한다
    Given 등록할 캠핑 사이트 정보가 있다
    When 캠핑 사이트를 등록하면
    Then 캠핑 사이트가 등록된다