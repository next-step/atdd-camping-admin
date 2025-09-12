# 💬 커밋 메시지 가이드

## 🎯 커밋 컨벤션 개요

이 프로젝트는 **명확하고 일관된 커밋 메시지**를 통해 개발 히스토리를 체계적으로 관리합니다. ATDD 방식의 단계별 개발 프로세스에 맞춘 커밋 가이드를 제공합니다.

## 📋 기본 커밋 메시지 구조

```
<type>: <subject>

<body>

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 🏷️ Type 분류

| Type       | 설명           | 사용 시점                       |
|------------|--------------|-----------------------------|
| `feat`     | 새로운 기능 추가    | HTTP 파일 작성, 최종 통합           |
| `test`     | 테스트 코드 추가/수정 | Gherkin, Steps, TestFixture |
| `refactor` | 코드 리팩토링      | Helper 시스템 개선, 구조 변경        |
| `docs`     | 문서 관련 변경     | README, 가이드 문서 작성           |
| `fix`      | 버그 수정        | 오류 수정, 로직 개선                |
| `chore`    | 기타 작업        | 빌드 설정, 의존성 관리               |

---

## 🔄 ATDD 단계별 커밋 가이드

### 1️⃣ HTTP 파일 작성 단계

```bash
feat: add {domain} HTTP requests for testing

- Add {HTTP_METHOD} /{endpoint} endpoint test
- Add {HTTP_METHOD} /{endpoint} endpoint test
- Include success and failure scenarios
- Add authorization headers for admin access

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

#### 실제 예제

```bash
feat: add rental admin HTTP requests for testing

- Add POST /admin/rentals endpoint test
- Add GET /admin/rentals endpoint test
- Include success and failure scenarios  
- Add authorization headers for admin access

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 2️⃣ Gherkin 시나리오 작성 단계

```bash
test: add {domain} {action} scenarios

- Add scenario for successful {action}
- Add scenario for {failure_case_1}
- Add scenario for {failure_case_2}
- Include DataTable for structured test parameters
- Cover business rules validation

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

#### 실제 예제

```bash
test: add rental creation scenarios

- Add scenario for successful rental record creation
- Add scenario for non-rental product failure case
- Add scenario for non-existent product failure case
- Include DataTable for structured test parameters
- Cover business rules validation

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 3️⃣ Cucumber Steps 구현 단계

```bash
test: implement {domain} Cucumber steps

- Add step definitions for {main_functionality}
- Add validation steps for business rules
- Integrate with {Domain}TestFixture for API calls
- Include proper error handling and assertions
- Add {specific_validation} logic

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

#### 실제 예제

```bash
test: implement rental Cucumber steps

- Add step definitions for rental record creation
- Add validation steps for business rules
- Integrate with RentalTestFixture for API calls
- Include proper error handling and assertions
- Add stock quantity validation logic

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 4️⃣ TestFixture 구현 단계

```bash
test: add {Domain}TestFixture for API abstraction

- Add {action} helper method
- Add {domain} listing with validation
- Add specific {domain} finder utility
- Integrate with ApiHelper for clean HTTP requests
- Include proper error handling and assertions

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

#### 실제 예제

```bash
test: add RentalTestFixture for API abstraction

- Add rental record creation helper method
- Add rental record listing with validation
- Add specific rental record finder utility
- Integrate with ApiHelper for clean HTTP requests
- Include proper error handling and assertions

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 5️⃣ 최종 통합 커밋

```bash
feat: implement {domain} {functionality} with ATDD approach

Complete implementation of {domain} functionality following ATDD methodology:

📋 Implementation Steps:
- ✅ HTTP request templates for manual testing
- ✅ Gherkin scenarios covering all business rules
- ✅ Cucumber step definitions with comprehensive validation
- ✅ TestFixture for API call abstraction
- ✅ Integration with existing helper system

🎯 Business Rules Covered:
- ✅ {business_rule_1}
- ✅ {business_rule_2}
- ✅ {business_rule_3}
- ✅ All edge cases and error scenarios included

🛠️ Technical Implementation:
- Uses ApiHelper for consistent HTTP request handling
- Follows established TestFixture patterns for maintainability
- Includes proper data validation and business rule enforcement
- Integrates seamlessly with existing test infrastructure

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

#### 실제 예제

```bash
feat: implement rental record management with ATDD approach

Complete implementation of rental record functionality following ATDD methodology:

📋 Implementation Steps:
- ✅ HTTP request templates for manual testing
- ✅ Gherkin scenarios covering all business rules  
- ✅ Cucumber step definitions with comprehensive validation
- ✅ TestFixture for API call abstraction
- ✅ Integration with existing helper system

🎯 Business Rules Covered:
- ✅ RENTAL type products can be rented with quantity validation
- ✅ SALE type products cannot be rented (proper error handling)
- ✅ Non-existent products return appropriate error responses
- ✅ Stock quantity decreases correctly after successful rental
- ✅ All edge cases and error scenarios included

🛠️ Technical Implementation:
- Uses ApiHelper for consistent HTTP request handling
- Follows established TestFixture patterns for maintainability
- Includes proper data validation and business rule enforcement
- Integrates seamlessly with existing test infrastructure

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

---

## 🔧 기술적 개선 커밋

### Helper 시스템 개선

```bash
refactor: improve ApiHelper with strategy pattern

- Extract HttpMethodStrategy interface
- Implement individual strategy classes for each HTTP method
- Add support method for dynamic strategy selection
- Refactor RestAssuredHelper to use for loop pattern
- Remove code duplication and improve maintainability

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 데이터베이스 관리 시스템 추가

```bash
feat: add Gradle-based database rollback system

- Add databaseSnapshot, databaseRestore, databaseReset tasks
- Create cleanup.sql for table data removal and sequence reset
- Create restore-initial-data.sql for initial state restoration
- Integrate with Cucumber tests for automatic rollback
- Add DatabaseTestHelper for programmatic DB management

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 문서화 작업

```bash
docs: create comprehensive project documentation

- Add /docs folder with structured documentation
- Create architecture.md for system overview
- Add testing-guide.md with ATDD process details
- Include helper-system.md with usage examples
- Add few-shot-examples.md with implementation patterns
- Create commit-guide.md for consistent commit messages

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

---

## 🚨 주의사항과 권장사항

### ✅ 좋은 커밋 메시지

```bash
# 구체적이고 명확한 설명
feat: add product inventory management API

- Add GET /admin/products for product listing
- Add POST /admin/products for product creation  
- Add PATCH /admin/products/{id} for stock updates
- Include validation for ProductType enum (RENTAL/SALE)
- Add proper error handling for duplicate products

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

```bash
# 비즈니스 규칙을 명확히 표현
test: add inventory validation scenarios

- Add scenario for successful product creation
- Add scenario for duplicate product name rejection
- Add scenario for invalid ProductType handling
- Add scenario for negative stock quantity validation
- Include DataTable for various product configurations

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### ❌ 피해야 할 커밋 메시지

```bash
# 너무 간략하고 모호함
fix: bug fix

# 의미 없는 메시지
test: add tests

# 여러 작업을 한번에 커밋
feat: add products and rentals and fix bugs
```

---

## 📊 커밋 메시지 체크리스트

### 단계별 체크리스트

#### HTTP 파일 작성 후

- [ ] `feat:` 타입 사용
- [ ] 테스트하는 엔드포인트 명시
- [ ] 성공/실패 시나리오 포함 여부 표시
- [ ] 인증 헤더 처리 방식 언급

#### Gherkin 시나리오 작성 후

- [ ] `test:` 타입 사용
- [ ] 비즈니스 규칙별 시나리오 나열
- [ ] DataTable 사용 여부 언급
- [ ] 예외 상황 커버 여부 표시

#### Cucumber Steps 구현 후

- [ ] `test:` 타입 사용
- [ ] TestFixture 연동 방식 설명
- [ ] 검증 로직 포함 여부 언급
- [ ] 에러 처리 방식 설명

#### TestFixture 구현 후

- [ ] `test:` 타입 사용
- [ ] ApiHelper 활용 방식 언급
- [ ] 제공하는 유틸리티 메서드 나열
- [ ] 에러 처리 및 검증 방식 설명

#### 최종 통합 커밋

- [ ] `feat:` 타입 사용
- [ ] 구현 단계별 체크리스트 포함
- [ ] 비즈니스 규칙 검증 내용 정리
- [ ] 기술적 구현 사항 요약

### 공통 체크리스트

- [ ] 명확하고 구체적인 제목
- [ ] 불릿 포인트로 구체적인 변경 사항 나열
- [ ] Claude Code 시그니처 포함
- [ ] 50자 내외의 간결한 제목
- [ ] 필요시 왜(Why) 그렇게 구현했는지 설명

---

## 🎯 실무 적용 가이드

### 개발 워크플로우

1. **기능 개발 시작** → HTTP 파일 작성 → 첫 번째 커밋
2. **비즈니스 분석 완료** → Gherkin 시나리오 작성 → 두 번째 커밋
3. **테스트 로직 구현** → Cucumber Steps 작성 → 세 번째 커밋
4. **API 추상화** → TestFixture 구현 → 네 번째 커밋
5. **기능 완성** → 통합 테스트 완료 → 최종 커밋

### 팀 협업 고려사항

- **일관성**: 모든 팀원이 동일한 패턴 사용
- **추적성**: 커밋 히스토리로 개발 과정 파악 가능
- **리뷰 용이성**: 단계별 커밋으로 코드 리뷰 효율성 향상
- **문제 해결**: 이슈 발생 시 정확한 변경 지점 파악 가능

### 자동화 도구 활용

```bash
# Git hooks를 통한 커밋 메시지 검증
# .git/hooks/commit-msg
#!/bin/sh
commit_regex='^(feat|test|refactor|docs|fix|chore):\s.{1,50}'

if ! grep -qE "$commit_regex" "$1"; then
    echo "❌ 커밋 메시지 형식이 올바르지 않습니다."
    echo "형식: <type>: <subject>"
    exit 1
fi
```

이 가이드를 따라 일관된 커밋 메시지를 작성하여 프로젝트의 히스토리를 체계적으로 관리하세요! 📝