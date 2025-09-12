# 🏕️ 초록 캠핑장 관리자 시스템 - CLAUDE 가이드

## 📚 문서 구성

이 프로젝트는 체계적인 문서화를 통해 ATDD 방식의 테스트 개발을 지원합니다.

### 📖 주요 문서들

| 문서                                            | 설명                   |
|-----------------------------------------------|----------------------|
| [📐 프로젝트 아키텍처](./docs/architecture.md)        | 전체 시스템 구조와 기술 스택     |
| [🧪 테스트 개발 가이드](./docs/testing-guide.md)      | ATDD 방식의 단계별 개발 프로세스 |
| [🛠️ 헬퍼 시스템 가이드](./docs/helper-system.md)     | 테스트 헬퍼 클래스 활용법       |
| [🎯 Few-Shot 예제](./docs/few-shot-examples.md) | 실제 구현 예제들            |
| [💬 커밋 메시지 가이드](./docs/commit-guide.md)       | 프로젝트 커밋 컨벤션          |

## 🚀 빠른 시작

### 새로운 기능 개발 시

1. **[테스트 개발 가이드](./docs/testing-guide.md)** 읽기
2. **[Few-Shot 예제](./docs/few-shot-examples.md)**에서 유사한 사례 찾기
3. **[헬퍼 시스템 가이드](./docs/helper-system.md)**로 도구 활용법 익히기
4. **[커밋 메시지 가이드](./docs/commit-guide.md)**로 올바른 커밋 작성
5. **문서 동기화 확인** - 새 기능 구현 후 관련 문서들 업데이트

### ATDD 개발 프로세스

```
1️⃣ HTTP 파일 작성 → 2️⃣ Gherkin 시나리오 → 3️⃣ Cucumber Steps → 4️⃣ TestFixture → 5️⃣ 커밋
```

## 📋 프로젝트 개요

### 기술 스택

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 (인메모리)
- **Testing**: Cucumber + RestAssured (ATDD 방식)
- **Build Tool**: Gradle
- **Authentication**: JWT

### 접속 정보

- **애플리케이션**: http://localhost:8081
- **H2 콘솔**: http://localhost:8081/h2-console
- **관리자 계정**: admin / admin123

## 🎯 핵심 원칙

### 테스트 작성 원칙

- ✅ **SpringBootTest 사용 금지** - 순수 RestAssured 사용
- ✅ **Helper 적극 활용** - 코드 중복 최소화
- ✅ **데이터 격리** - 각 시나리오별 독립적 실행
- ✅ **의미 있는 메서드명** - 한글 메서드명으로 가독성 향상

### 개발 워크플로우

1. **HTTP 파일 작성** → API 동작 확인 → 커밋
2. **Gherkin 시나리오 작성** → 비즈니스 요구사항 정의 → 커밋
3. **Steps 구현** → 테스트 로직 작성 → 커밋
4. **TestFixture 활용** → API 호출 추상화 → 커밋
5. **최종 통합** → 전체 기능 완성 → 통합 커밋

## 🛠️ 핵심 도구들

### ApiHelper - HTTP 요청 단순화

```java
// 인증이 필요한 요청
createExtractableResponseWithAuthorization("POST","/admin/rentals",body);

// 인증이 필요 없는 요청
createExtractableResponse("GET","/health");
```

### 데이터베이스 관리

```bash
# 테스트 실행 (자동 롤백)
./gradlew test

# Cucumber 테스트
./gradlew test --tests "*CucumberTestRunner"
```

### 권장 네이밍 컨벤션

```java
// TestFixture 메서드
public static ExtractableResponse<Response> 대여_기록_목록_조회()

public static Map<String, Object> 특정_대여_기록_조회(long rentalId)

// Steps 메서드
@When("관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.")
@Then("대여 기록이 생성된다.")
```

## 📝 문서 동기화 가이드

### 🔄 자동 문서 동기화 프로세스

코드 변경 시 문서가 항상 최신 상태를 유지하도록 다음 프로세스를 따르세요:

#### 1️⃣ 기능 구현 전 체크리스트

```bash
# 현재 문서들이 실제 프로젝트와 일치하는지 확인
- [ ] CLAUDE.md의 문서 링크들이 실제 존재하는가?
- [ ] 기술 스택 정보가 build.gradle과 일치하는가?
- [ ] 접속 정보가 application.yml과 일치하는가?
- [ ] Gradle 태스크가 실제 정의되어 있는가?
```

#### 2️⃣ 기능 구현 중 문서 업데이트

- **새로운 엔드포인트 추가** → `docs/architecture.md` API 목록 업데이트
- **새로운 테스트 패턴 추가** → `docs/few-shot-examples.md` 예제 추가
- **헬퍼 클래스 변경** → `docs/helper-system.md` 사용법 업데이트
- **테스트 프로세스 변경** → `docs/testing-guide.md` 단계별 가이드 수정

#### 3️⃣ 구현 완료 후 최종 검증

```bash
# 문서 동기화 상태 확인
- [ ] 모든 문서 링크 정상 작동
- [ ] 예제 코드가 실제 코드베이스와 일치
- [ ] 설정 정보가 application.yml과 동기화
- [ ] 새로운 기능의 사용법이 문서에 반영
```

#### 4️⃣ 커밋 전 마지막 점검

```bash
# 문서 일관성 검증
./gradlew test                    # 모든 예제가 동작하는지 확인
git diff --name-only             # 변경된 파일들과 관련 문서들 확인
```

### 🎯 문서별 업데이트 트리거

| 변경 사항                 | 업데이트할 문서                                   |
|-----------------------|--------------------------------------------|
| 새로운 Controller/API 추가 | `architecture.md`                          |
| 테스트 Helper 메서드 변경     | `helper-system.md`                         |
| 새로운 테스트 패턴 도입         | `few-shot-examples.md`, `testing-guide.md` |
| Gradle 설정 변경          | `CLAUDE.md` (기술 스택)                        |
| application.yml 변경    | `CLAUDE.md` (접속 정보)                        |
| 새로운 도메인/엔티티 추가        | `architecture.md`                          |

---

**더 자세한 내용은 [docs/](./docs/) 폴더의 각 가이드 문서를 참고하세요!** 📚