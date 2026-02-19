# auth.md

## 1) 현재 권한 처리 요약

현재 프로젝트의 "권한 처리"는 정확히 말하면 **JWT 기반 인증(Authentication)** 이 중심이고, **세분화된 인가(Authorization/RBAC)** 는 거의 없는 상태다.

- 인증 성공 조건:
  - `admin.username`, `admin.password` 값과 로그인 요청이 일치해야 함
  - 위치: `src/main/resources/application.yml`, `src/test/resources/application.yml`
- 로그인 API:
  - `POST /auth/login`
  - 위치: `src/main/java/com/camping/admin/controller/AuthController.java`
- 토큰 발급:
  - `JwtService.generateToken(username)`로 JWT 생성
  - claim에 `role=ADMIN`이 들어가지만, 현재 서버에서 이 role을 검증/분기하지는 않음
  - 위치: `src/main/java/com/camping/admin/security/JwtService.java`

## 2) 요청 보호 방식

- 필터 등록:
  - `JwtAuthFilter`를 `/*`에 적용
  - 위치: `src/main/java/com/camping/admin/config/WebConfig.java`
- 토큰 추출 우선순위:
  - `Authorization: Bearer <token>` 헤더 우선
  - 없으면 `AUTH_TOKEN` 쿠키 fallback
  - 위치: `src/main/java/com/camping/admin/security/JwtAuthFilter.java`
- 예외 경로(인증 제외):
  - `/auth/login`, `/login`, 정적 리소스, `/h2-console/**`, `/`
- 인증 실패 응답:
  - HTML 요청(`Accept: text/html`)이면 `/login` 리다이렉트
  - API 요청이면 `401` + JSON 에러 응답

## 3) 인가(Authorization) 관점에서 현재 상태

- 유효한 JWT만 있으면 보호된 경로 대부분 접근 가능
- "관리자/직원/읽기전용" 같은 역할별 권한 분리는 없음
- 즉, 현재 구조는 **단일 관리자 계정 기반 인증**에 가깝다

## 4) Cucumber에서 권한 처리(현재 적용 상태)

권한 처리는 테스트 공통 훅에서 한 번 로그인하고, 이후 모든 API 요청에 토큰을 붙이는 방식으로 되어 있다.

- 시나리오 시작 전 JWT 발급:
  - `HookSteps.beforeScenario()`에서 `/auth/login` 호출
  - `context.jwtToken`에 저장
  - 위치: `src/test/java/com/camping/admin/steps/HookSteps.java`
- 인증 요청 헬퍼:
  - `TestContext.authRequest()`가 `Authorization: Bearer ...`를 자동 세팅
  - 위치: `src/test/java/com/camping/admin/support/TestContext.java`

## 5) `campsiteSteps`에서는 어떻게 처리할까?

현재 `campsiteSteps`는 이미 좋은 패턴으로 적용되어 있다.

- 보호 API 호출 시 `context.authRequest()` 사용
  - `GET /admin/campsites`
  - `POST /admin/campsites`
  - `PUT /admin/campsites/{id}`
- 생성 후 `id`를 `context.campsiteId`에 저장해 다음 스텝에서 재사용
- 위치: `src/test/java/com/camping/admin/steps/campsiteSteps.java`

### 권장 패턴

1. 인증 필요한 호출은 모두 `context.authRequest()`로 시작
2. 시나리오 간 공유가 필요한 식별자(`id`)는 `context`에 저장
3. 권한 실패 시나리오도 별도로 작성
   - 토큰 없이 호출 -> `401` 검증
   - 잘못된 토큰 호출 -> `401` 검증

예시:

```java
context.response = context.authRequest().get("/admin/campsites");
```

토큰 누락 시나리오 예시:

```java
context.response = RestAssured.given()
        .contentType(ContentType.JSON)
        .get("/admin/campsites");
```

## 6) 정리

- 현재 프로젝트는 JWT로 "로그인 여부"를 확인하는 구조다.
- `campsiteSteps` 같은 ATDD 스텝에서는 이미 공통 토큰 패턴(`HookSteps` + `TestContext.authRequest`)으로 잘 처리 중이다.
- 이후 권한 모델을 확장하려면 role claim 검증 및 엔드포인트별 인가 규칙이 추가되어야 한다.
