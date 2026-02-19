Feature: API 인증
  모든 관리 API는 유효한 JWT 토큰이 있어야 접근할 수 있다.

  Scenario: 올바른 자격증명으로 로그인하면 토큰이 발급된다
    When 올바른 자격증명으로 로그인한다 (POST "/auth/login")
    Then 로그인에 성공한다 (200)
    And JWT 토큰이 발급된다

  Scenario: 잘못된 비밀번호로 로그인하면 실패한다
    When 잘못된 자격증명으로 로그인한다 (POST "/auth/login")
    Then 로그인이 거부된다 (401)

  Scenario: 토큰 없이 요청하면 인증 오류가 반환된다
    When 토큰 없이 관리 API를 요청한다 (GET "/admin/campsites")
    Then 인증 오류가 반환된다 (401)

  Scenario: 유효하지 않은 토큰으로 요청하면 인증 오류가 반환된다
    When 유효하지 않은 토큰으로 관리 API를 요청한다 (GET "/admin/campsites")
    Then 인증 오류가 반환된다 (401)
