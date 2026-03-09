Feature: 캠프사이트 상태 관리
  캠핑장 관리자는 캠프사이트의 운영 상태를 변경할 수 있다.

  Background:
    Given 캠프사이트가 등록되어 있다

  # PATCH "/admin/campsites/{id}/status"

  Scenario: 캠프사이트 상태를 점검중으로 변경한다
    When 캠프사이트 상태를 점검중으로 변경한다
    Then 수정에 성공한다
    And 캠프사이트 상태가 점검중이다

  Scenario: 캠프사이트 상태를 운영종료로 변경한다
    When 캠프사이트 상태를 운영종료로 변경한다
    Then 수정에 성공한다
    And 캠프사이트 상태가 운영종료이다

  Scenario: 점검중인 캠프사이트를 이용가능으로 변경한다
    Given 캠프사이트가 점검중 상태이다
    When 캠프사이트 상태를 이용가능으로 변경한다
    Then 수정에 성공한다
    And 캠프사이트 상태가 이용가능이다

  Scenario: 유효하지 않은 상태값으로 변경하면 실패한다
    When 유효하지 않은 캠프사이트 상태로 변경한다
    Then 캠프사이트 상태 변경이 거부된다
