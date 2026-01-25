Feature: 예약 관리
  캠핑장 관리자가 예약을 관리하는 기능

  Background:
    Given 관리자가 로그인했다

  # === 예약 목록 조회 ===
  Scenario: 예약 목록을 조회하면 성공한다
    When 예약 목록을 조회하면
    Then 목록이 조회된다

  # === 예약 상태 변경 ===
  Scenario: 예약 상태를 변경하면 성공한다
    Given 확정된 예약이 존재한다
    When 해당 예약을 체크인 상태로 변경하면
    Then 요청이 성공한다

  Scenario: 존재하지 않는 예약의 상태를 변경하면 실패한다
    Given 존재하지 않는 예약 ID를 사용한다
    When 해당 예약을 체크인 상태로 변경하면
    Then 요청이 실패한다

  Scenario: 빈 상태값으로 변경하면 실패한다
    Given 확정된 예약이 존재한다
    When 해당 예약을 빈 상태로 변경하면
    Then 요청이 실패한다
