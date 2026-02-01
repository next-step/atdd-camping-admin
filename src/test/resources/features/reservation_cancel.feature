@reservation
Feature: 관리자의 예약 취소 기능

  Background:
    Given 관리자가 로그인되어 있다

  # ========================================
  # 현재 동작하는 기능 (Safety Net)
  # ========================================

  @happy-path @smoke
  Scenario: 대기 상태의 예약을 취소하면 성공한다
    Given "PENDING" 상태의 예약이 존재한다
    When 관리자가 해당 예약을 취소하면
    Then 예약 상태가 "CANCELLED"로 변경된다

  @happy-path @smoke
  Scenario: 확정 상태의 예약을 취소하면 성공한다
    Given "CONFIRMED" 상태의 예약이 존재한다
    When 관리자가 해당 예약을 취소하면
    Then 예약 상태가 "CANCELLED"로 변경된다
