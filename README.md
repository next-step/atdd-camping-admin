# 🏕️ 초록 캠핑장 관리자 시스템

## 📖 프로젝트 소개
초록 캠핑장 체인의 관리자를 위한 통합 관리 시스템입니다. 예약 관리, 사이트 관리, 매출 통계 등의 기능을 제공합니다.

> ⚠️ **주의**: 이 프로젝트는 ATDD 학습을 위한 레거시 시스템 예제입니다. 의도적으로 복잡한 구조와 중복 코드가 포함되어 있습니다.

## 🚀 시작하기

### 필수 요구사항
- Java 17 이상
- Gradle 7.0 이상

### 실행 방법
```bash
# 프로젝트 클론
git clone [repository-url]
cd camping-admin-system

# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

### 접속 정보
- **애플리케이션**: http://localhost:8081
- **H2 콘솔**: http://localhost:8081/h2-console
    - JDBC URL: `jdbc:h2:mem:testdb`
    - Username: `sa`
    - Password: (빈 값)

## 👤 테스트 계정

| 역할 | 이메일 | 비밀번호 | 권한 |
|------|--------|----------|------|
| 관리자 | admin | admin123 | 모든 캠핑장 관리 |

## 🏗️ 시스템 구조

### 주요 기능
1. 대시 보드
2. 상품 관리
3. 사이트 관리
4. 예약 관리
5. 매출 조회

## 테스트 구조
### 테스트 러너 클래스
테스트 러너 클래스는 테스트를 실행하는 진입점 역할을 하는 클래스입니다.  
Cucumber에서 테스트를 실행할 때 어떤 feature 파일들을 실행하고, 어떤 step definitions를 사용할지, 어떤 설정을 적용할지를 정의하는 클래스입니다.
#### 왜 필요한가?
* 테스트 실행 진입점: 테스트를 시작하는 시작점 역할  
* 설정 관리: Cucumber의 다양한 설정을 한 곳에서 관리  
* 테스트 그룹화: 관련된 테스트들을 그룹화하여 실행  
* CI/CD 통합: 자동화된 테스트 실행을 위한 표준화된 진입점  

## IntelliJ에서 인수테스트 실행시 breakpoint 안 걸리는 이유 및 해결법

### 💡 IDE에서 디버깅이 안 걸리는 이유와 해결책

핵심 문제:
현재 Cucumber 테스트는 RestAssured를 사용해서 HTTP 요청을 보내는 구조입니다.  
이는 외부 프로세스로 실행되는 Spring Boot 애플리케이션과 통신하는 방식이므로,  
IDE에서 TestRunner만 디버그 실행해서는 브레이크포인트가 걸리지 않습니다.

### 🎯 해결 방법 : Spring Boot 애플리케이션을 별도로 디버그 모드로 실행

- 1단계: 애플리케이션을 디버그 모드로 실행
    - > ./gradlew bootRun --debug-jvm

- 2단계: IDE에서 Remote Debug 연결
    - > IntelliJ: Run → Attach to Process → localhost:5005  
      또는 Run → Edit Configurations → Add → Remote JVM Debug

- 3단계: 브레이크포인트 설정 후 Cucumber 테스트 실행
    - `RentalService.createRental()`에 브레이크포인트 설정
    - IDE에서 TestRunner 실행
