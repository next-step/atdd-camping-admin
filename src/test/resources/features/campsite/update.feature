Feature: 캠핑장 수정
  Scenario: 관리자가 캠핑장 정보를 수정한다
    Given 등록된 캠핑장이 있다
    When 관리자가 캠핑장 정보를 수정한다
    Then 캠핑장 정보가 수정된다

  Scenario: 존재하지 않는 캠핑장을 수정할 수 없다
    When 관리자가 존재하지 않는 캠핑장을 수정한다
    Then 캠핑장 수정이 거부된다