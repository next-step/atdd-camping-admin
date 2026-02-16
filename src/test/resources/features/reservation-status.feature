Feature: 예약 상태 변경
  캠핑장 관리자는 예약의 상태를 변경할 수 있다.
  상태 변경은 유효한 값과 올바른 전이 규칙을 따라야 한다.

  Background:
    Given 캠프사이트가 등록되어 있다
    And "CONFIRMED" 상태의 예약이 존재한다

  # ───────────────────────────────────────
  # 정상 시나리오
  # ───────────────────────────────────────

  Scenario: 확정된 예약을 체크인 처리한다
    When 예약의 상태를 "CHECKED_IN"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 성공한다 (200)
    And 예약 상태는 "CHECKED_IN"이다

  Scenario: 체크인된 예약을 체크아웃 처리한다
    Given 예약 상태가 "CHECKED_IN"이다
    When 예약의 상태를 "CHECKED_OUT"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 성공한다 (200)
    And 예약 상태는 "CHECKED_OUT"이다

  # ───────────────────────────────────────
  # 예외: 유효하지 않은 상태 값
  # ───────────────────────────────────────

  Scenario: 존재하지 않는 상태 값으로 변경을 시도하면 실패한다
    When 예약의 상태를 "HELLO_WORLD"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)
    And 오류 메시지에 "유효하지 않은 상태" 내용이 포함된다

  Scenario: 소문자 상태 값으로 변경을 시도하면 실패한다
    When 예약의 상태를 "confirmed"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 숫자로 상태 변경을 시도하면 실패한다
    When 예약의 상태를 "12345"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 특수문자가 포함된 상태 값으로 변경을 시도하면 실패한다
    When 예약의 상태를 "CONFIRMED!@#"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: SQL 인젝션 문자열로 상태 변경을 시도하면 실패한다
    When 예약의 상태를 "'; DROP TABLE reservations;--"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  # ───────────────────────────────────────
  # 예외: 빈 값 / null 처리
  # ───────────────────────────────────────

  Scenario: 빈 문자열로 상태 변경을 시도하면 실패한다
    When 예약의 상태를 ""으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 공백 문자열로 상태 변경을 시도하면 실패한다
    When 예약의 상태를 "   "으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: status 필드 없이 요청하면 실패한다
    When status 필드 없이 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 빈 요청 본문으로 요청하면 실패한다
    When 빈 요청 본문으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  # ───────────────────────────────────────
  # 예외: 허용되지 않는 상태 전이
  # ───────────────────────────────────────

  Scenario: 취소된 예약을 확정 상태로 변경할 수 없다
    Given 예약 상태가 "CANCELLED"이다
    When 예약의 상태를 "CONFIRMED"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)
    And 오류 메시지에 "상태 전이" 내용이 포함된다

  Scenario: 체크아웃된 예약을 대기 상태로 변경할 수 없다
    Given 예약 상태가 "CHECKED_OUT"이다
    When 예약의 상태를 "WAITING"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 확정된 예약을 체크인 없이 바로 체크아웃할 수 없다
    When 예약의 상태를 "CHECKED_OUT"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 거절된 예약을 체크인할 수 없다
    Given 예약 상태가 "REJECTED"이다
    When 예약의 상태를 "CHECKED_IN"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 체크아웃된 예약을 다시 체크인할 수 없다
    Given 예약 상태가 "CHECKED_OUT"이다
    When 예약의 상태를 "CHECKED_IN"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)

  Scenario: 같은 상태로 중복 변경을 시도하면 실패한다
    When 예약의 상태를 "CONFIRMED"으로 변경 요청하면 (PATCH "/admin/reservations/{id}/status")
    Then 요청이 실패한다 (400)
    And 오류 메시지에 "동일한 상태" 내용이 포함된다

  # ───────────────────────────────────────
  # 예외: 존재하지 않는 예약
  # ───────────────────────────────────────

  Scenario: 존재하지 않는 예약의 상태를 변경하면 실패한다
    When 존재하지 않는 예약 ID 99999로 상태를 "CONFIRMED"으로 변경 요청하면 (PATCH "/admin/reservations/99999/status")
    Then 요청이 실패한다 (404)
