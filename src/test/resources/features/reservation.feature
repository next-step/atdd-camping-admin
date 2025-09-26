Feature: 관리자 예약 관리 기능

  Rule: 예약 조회 기능
    Background:
      Given 관리자가 로그인을 하였다.

    Scenario: 관리자가 특정 예약을 조회하면, 해당 예약이 조회된다.
      Given 예약 ID 1 이 존재한다.
      When 관리자가 해당 예약을 조회한다.
      Then 예약 ID 1 의 상세 정보가 조회된다.

    Scenario: 관리자가 존재하지 않는 예약을 조회하면, 예외가 발생한다.
      Given 존재하지 않는 예약 ID 9999 가 있다.
      When 관리자가 해당 예약을 조회한다.
      Then 에러 응답이 발생한다.

  Rule: 예약 상태 변경 기능
    Background:
      Given 관리자가 로그인을 하였다.

    Scenario: 관리자가 예약 상태를 변경하면, 예약의 상태가 해당 상태로 변경된다.
      Given WAITING 상태인 예약이 존재한다.
      When 관리자가 WAITING 상태인 예약 상태를 PENDING 상태로 변경한다.
      Then 예약 상태가 PENDING 으로 변경된다.

    Scenario: 관리자가 존재하지 않는 예약의 상태를 변경하면, 예외가 발생한다.
      Given 존재하지 않는 예약 ID 9999 가 있다.
      When 관리자가 예약 ID 9999 의 상태를 PENDING 상태로 변경한다.
      Then 에러 응답이 발생한다.

    Scenario: 관리자가 잘못된 예약 상태로 변경하면, 예외가 발생한다.
      When 특정 예약에 대해 잘못된 상태로 예약 상태를 변경한다.
      Then 잘못된 요청 응답이 발생한다.