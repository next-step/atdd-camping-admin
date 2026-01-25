Feature: 예약 상태 관리
  Background:
    Given 캠핑장에 'A-01' 사이트가 등록되어 있다
    And 관리자 로그인이 되어 있다

  Scenario: 예약 상태를 'CONFIRMED'으로 변경한다
    Given 사이트 번호가 'A-01'인 캠핑장에 '홍길동' 이름으로 예약되어 있다
    When 관리자가 예약을 확정하면
    Then 예약의 상태가 확정된다
