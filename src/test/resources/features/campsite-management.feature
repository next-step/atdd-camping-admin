Feature: 캠프사이트 관리
  캠핑장 관리자는 캠프사이트를 조회, 등록, 수정할 수 있다.

  Scenario: 등록된 캠프사이트가 없으면 빈 목록이 반환된다
    When 캠프사이트 목록을 조회한다
    Then 조회에 성공한다
    And 빈 목록이 반환된다

  Scenario: 캠프사이트 목록을 조회한다
    Given 캠프사이트 2개가 등록되어 있다
    When 캠프사이트 목록을 조회한다
    Then 조회에 성공한다
    And 캠프사이트 2개가 반환된다

  Scenario: 새로운 캠프사이트를 등록한다
    When 새로운 캠프사이트를 등록한다
    Then 캠프사이트가 생성된다
    And 생성된 캠프사이트 정보가 반환된다

  Scenario: 캠프사이트 정보를 수정한다
    Given 캠프사이트가 등록되어 있다
    When 캠프사이트 정보를 수정한다
    Then 수정에 성공한다
    And 수정된 캠프사이트 정보가 반환된다

  # ───────────────────────────────────────
  # 예외 시나리오
  # ───────────────────────────────────────

  # TODO: GlobalExceptionHandler 구현 필요 — IllegalArgumentException → 404
  # Scenario: 존재하지 않는 캠프사이트를 수정하면 실패한다
  #   When 존재하지 않는 캠프사이트를 수정한다
  #   Then 캠프사이트를 찾을 수 없다

  # TODO: CampsiteAdminController.createCampsite — siteNumber 필수값 검증 추가 (null → 400)
  # Scenario: siteNumber 없이 캠프사이트를 등록하면 실패한다
  #   When siteNumber 없이 캠프사이트를 등록한다
  #   Then 등록이 거부된다

  # TODO: CampsiteAdminController.createCampsite — siteNumber 중복 체크 추가 (중복 → 400)
  # Scenario: 중복 siteNumber로 캠프사이트를 등록하면 실패한다
  #   Given 캠프사이트가 등록되어 있다
  #   When 중복 siteNumber로 캠프사이트를 등록한다
  #   Then 등록이 거부된다

  # TODO: CampsiteAdminController.createCampsite — maxPeople 양수 검증 추가 (음수 → 400)
  # Scenario: 음수 최대 인원으로 캠프사이트를 등록하면 실패한다
  #   When 음수 최대 인원으로 캠프사이트를 등록한다
  #   Then 등록이 거부된다
