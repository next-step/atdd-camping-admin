# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 코드 작업을 할 때 필요한 가이드를 제공합니다.

## 프로젝트 개요

ATDD (Acceptance Test Driven Development) 학습을 위해 구축된 Spring Boot 캠핑 관리 어드민 시스템입니다. Java 17, Spring Boot 3.2.0을 사용하며, 교육 목적으로 의도적으로 복잡한 레거시 구조를 포함하고 있습니다.

## 개발 명령어

### 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 클린 빌드
./gradlew clean build
```

### 테스트
```bash
# 모든 테스트 실행 (Cucumber ATDD 테스트 포함)
./gradlew test

# 상세 출력과 함께 테스트 실행
./gradlew test --info

# 특정 Cucumber 피처 실행
./gradlew test --tests "com.camping.admin.CucumberTestRunner"
```

### 데이터베이스 접근
- **애플리케이션**: http://localhost:8080 (application.yml에서 설정된 포트)
- **H2 콘솔**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - 사용자명: `sa`
  - 비밀번호: (빈 값)

### 관리자 접근
- **사용자명**: admin
- **비밀번호**: admin123

## 아키텍처 개요

### 패키지 구조
```
src/main/java/com/camping/admin/
├── CampingAdminServiceApplication.java  # Spring Boot 메인 클래스
├── config/                             # 설정 클래스들
├── controller/                         # REST 컨트롤러 & 웹 컨트롤러  
├── domain/                            # 도메인 엔티티와 열거형
│   ├── entity/                        # JPA 엔티티
│   └── enums/                         # 도메인 열거형
├── dto/                              # 데이터 전송 객체
├── repository/                       # JPA 저장소
├── security/                         # 보안 설정 & JWT
├── service/                          # 비즈니스 로직 서비스
└── web/                             # 웹 관련 클래스
```

### 주요 기술스택
- **프레임워크**: Spring Boot 3.2.0 (Spring Web, Data JPA, Validation, Thymeleaf 포함)
- **데이터베이스**: H2 인메모리 데이터베이스 (`data.sql`로 자동 초기화)
- **인증**: 커스텀 보안 설정을 포함한 JWT 기반
- **테스트**: ATDD용 Cucumber 7.14.0, JUnit Platform Suite, RestAssured 5.3.2
- **빌드**: Lombok 지원 포함 Gradle

### 핵심 기능
1. 대시보드 관리
2. 상품 관리  
3. 사이트 관리
4. 예약 관리
5. 매출 보고

### 테스트 전략
이 프로젝트는 Cucumber를 사용한 ATDD 원칙을 따릅니다:
- **테스트 러너**: `CucumberTestRunner.java`가 JUnit Platform Suite 사용
- **피처 파일**: `src/test/resources/features/` 위치
- **스텝 정의**: `src/test/java/com/camping/admin/steps/` 위치
- **BDD 언어**: 한국어 (예약, 관리자 등)

### 설정 참고사항
- **포트**: 기본 8080 (`application.yml` 참조)
- **데이터베이스**: `create-drop` DDL 모드와 SQL 로깅 활성화된 H2
- **JWT**: 개발용 비밀 키로 30분 만료
- **로깅**: 애플리케이션 코드 DEBUG 레벨, SQL 쿼리 로깅 활성화

### 디자인 가이드라인
`design.md` 파일에 특정 UI/UX 가이드라인이 포함되어 있습니다:
- 녹색 그라데이션 색상 스킴 (`#2d6a4f`에서 `#52b788`)
- Segoe UI 폰트 스택 사용 타이포그래피
- 둥근 모서리를 가진 카드 기반 레이아웃 패턴
- 일관된 버튼 스타일과 반응형 디자인 패턴

### 개발 참고사항
- 의도적인 복잡성을 포함한 레거시 시스템 예제
- JPA 엔티티와 함께 표준 Spring Boot 관례 사용
- SQL 스크립트를 통한 포괄적인 데이터 초기화 포함
- 관리자 접근을 위한 JWT 인증 구현
- 모든 테스트는 BDD/Cucumber 구조를 유지해야 함
