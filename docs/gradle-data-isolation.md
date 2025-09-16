# 🔄 테스트 데이터 격리 가이드

## 📋 개요

SpringBootTest를 사용하지 않으면서도 각 Cucumber 시나리오마다 완전한 데이터 격리를 보장하는 **HTTP API 기반** 솔루션입니다.

## 🎯 현재 구현된 격리 메커니즘

### 🔄 HTTP API 기반 자동 초기화 (현재 방식)

- **@After Hook 자동화**: 각 시나리오 완료 후 HTTP API로 DB 초기화
- **애플리케이션 1회 시작**: 테스트 중 애플리케이션을 재시작하지 않아 빠름
- **완전한 데이터 격리**: 각 시나리오가 독립적으로 실행됨

### 핵심 구현 요소

1. **Hooks.java** - @After에서 HTTP API 호출
2. **DatabaseAdminController** - `/admin/database/reset` 엔드포인트 제공
3. **DatabaseCleaner Bean** - 실제 DB 초기화 로직

### ⚡ 성능 비교

| 방식 | 애플리케이션 시작 | 격리 수준 | 테스트 속도 | 상태 |
|-----|----------------|-----------|------------|------|
| HTTP API 기반 | 1회 | 완전 격리 | 빠름 ⚡ | **현재 방식** |
| JVM 격리 방식 | 시나리오마다 | 완전 격리 | 느림 | 레거시 |

## 🚀 사용법

### 격리된 테스트 실행 (권장)
```bash
# 각 시나리오마다 완전히 독립적으로 실행
./gradlew isolatedCucumberTest
```

### 기존 방식 테스트 (호환성 유지)
```bash
# 기존 cucumberTest (데이터 격리 제한적)
./gradlew cucumberTest
```

## 📊 HTTP API 기반 동작 원리

### 1️⃣ 테스트 시작 시 (1회만)
```
🚀 Spring Boot 애플리케이션 시작 (1회)
🗄️ H2 메모리 DB 스키마 생성 (create-drop)
📋 data.sql 실행 (초기 데이터 로드)
🔑 JWT 토큰 생성 및 컨텍스트에 저장
```

### 2️⃣ 각 시나리오 실행 중
```
🧪 순수 RestAssured로 HTTP API 호출
📝 데이터 변경 (대여 기록 생성, 예약 추가 등)
✅ 테스트 검증
```

### 3️⃣ 각 시나리오 완료 후 (@After Hook)
```
🧹 HTTP POST /admin/database/reset 호출
🔑 JWT 토큰으로 인증
🗑️ 모든 테이블 TRUNCATE
🔢 ID 시퀀스 1로 리셋
📋 data.sql 재실행으로 초기 데이터 복원
✅ 다음 시나리오 준비 완료
```

### 💡 장점

- **빠른 실행**: 애플리케이션을 매번 재시작하지 않음
- **완전한 격리**: 각 시나리오가 독립적으로 실행
- **안전한 초기화**: JWT 인증을 통한 보안 확보
- **자동화**: 개발자가 수동 개입할 필요 없음

## 🔧 핵심 설정

### build.gradle.kts의 현재 설정

```kotlin
// TestSets 플러그인으로 격리된 테스트 환경 구성
testSets {
    create("isolatedCucumber") {
        dirName = "test"
    }
}

// 격리된 Cucumber 테스트 태스크
tasks.register<Test>("isolatedCucumberTest") {
    group = "verification"
    description = "격리된 Cucumber 테스트 실행 - 각 시나리오마다 @After Hook으로 DB 자동 초기화"

    useJUnitPlatform()
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    // 각 시나리오마다 새 JVM으로 완전 격리
    forkEvery = 1

    // JVM 옵션 최적화
    jvmArgs = listOf(
        "-XX:TieredStopAtLevel=1",  // C1 컴파일러만 사용
        "-XX:+UseParallelGC"        // 병렬 GC 사용
    )

    // Spring 설정 - data.sql 자동 재실행 보장
    systemProperty("spring.jpa.hibernate.ddl-auto", "create-drop")
    systemProperty("spring.sql.init.mode", "always")

    systemProperty("cucumber.junit-platform.naming-strategy", "long")
}

// 기존 방식 Cucumber 테스트 (호환성 유지)
tasks.register<Test>("cucumberTest") {
    group = "verification"
    description = "기존 방식 Cucumber 테스트 - @After Hook으로 DB 자동 초기화"

    useJUnitPlatform()
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    systemProperty("cucumber.junit-platform.naming-strategy", "long")
}
```

### 🔑 핵심 포인트

- **`forkEvery = 1`**: 각 시나리오마다 새로운 JVM 실행 (선택사항)
- **HTTP API 기반**: 실제 데이터 초기화는 `@After` Hook에서 HTTP API 호출
- **두 가지 옵션**: JVM 격리 vs 단일 JVM에서 HTTP API 초기화

## 📈 성능 최적화 팁

### JVM 시작 시간 최적화
```gradle
isolatedCucumberTest {
    // JVM 옵션 최적화
    jvmArgs = [
        '-XX:TieredStopAtLevel=1',  // C1 컴파일러만 사용
        '-Xverify:none',            // 바이트코드 검증 생략
        '-XX:+UseParallelGC'        # 병렬 GC 사용
    ]
}
```

### 선택적 테스트 실행
```bash
# 특정 태그만 실행
./gradlew isolatedCucumberTest -Dcucumber.filter.tags="@smoke"

# 특정 시나리오만 실행
./gradlew isolatedCucumberTest -Dcucumber.filter.name="대여 기록 생성"
```

## 🚨 주의사항

### ❌ 피해야 할 것들
- SpringBootTest 사용 (순수 RestAssured 유지)
- @DirtiesContext 어노테이션 (Gradle 격리로 대체됨)
- 수동 DB 초기화 코드 (자동 처리됨)

### ✅ 권장사항
- 시나리오별 데이터 의존성 최소화
- 테스트 데이터를 data.sql에 집중
- HTTP API를 통한 검증에 집중

## 🔍 문제 해결

### 자주 발생하는 문제들

#### 1. "테이블을 찾을 수 없습니다" 에러
```
해결: application.yml의 ddl-auto가 create-drop으로 설정되었는지 확인
```

#### 2. data.sql이 실행되지 않음
```
해결: spring.sql.init.mode=always 설정 확인
```

#### 3. 시나리오 간 데이터 오염
```
해결: isolatedCucumberTest 사용 (forkEvery=1 설정 확인)
```

## 📋 마이그레이션 가이드

### 기존 테스트에서 마이그레이션
```java
// 기존 방식 (변경 없음)
@Given("관리자는 캠핑장에 로그인한다")
public void 관리자는_캠핑장에_로그인한다() {
    // RestAssured 코드 그대로 사용
}

// Hooks.java도 수정 불필요!
// Gradle이 자동으로 데이터 격리 처리
```

### 실행 명령어만 변경
```bash
# 기존
./gradlew cucumberTest

# 새로운 방식
./gradlew isolatedCucumberTest
```

## 🎉 기대 효과

- ✅ **완전한 데이터 격리**: 시나리오 간 영향 없음
- ✅ **SpringBootTest 불필요**: 순수 RestAssured 유지
- ✅ **자동 초기화**: data.sql 자동 재실행
- ✅ **기존 코드 호환**: 테스트 코드 수정 불필요
- ✅ **운영환경 유사**: HTTP API를 통한 실제 통합 테스트