# 📐 프로젝트 아키텍처

## 🏗️ 시스템 개요

### 기술 스택

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 (인메모리)
- **Testing**: Cucumber + RestAssured (ATDD 방식)
- **Build Tool**: Gradle
- **Authentication**: JWT

### 애플리케이션 포트

- **서버**: http://localhost:8081
- **H2 콘솔**: http://localhost:8081/h2-console

## 📁 프로젝트 구조

```
atdd-camping-admin/
├── 📁 src/main/java/com/camping/admin/
│   ├── 🎯 controller/              # REST API 엔드포인트
│   │   ├── AuthController.java     # 인증 (로그인)
│   │   ├── CampsiteAdminController.java
│   │   ├── ProductAdminController.java  
│   │   ├── RentalAdminController.java
│   │   ├── ReservationAdminController.java
│   │   ├── RevenueAdminController.java
│   │   └── SalesController.java
│   ├── 🏗️ domain/
│   │   ├── entity/                 # JPA 엔티티
│   │   │   ├── Campsite.java      # 캠프사이트
│   │   │   ├── Customer.java      # 고객
│   │   │   ├── Product.java       # 상품  
│   │   │   ├── RentalRecord.java  # 대여 기록
│   │   │   ├── Reservation.java   # 예약
│   │   │   └── SalesRecord.java   # 판매 기록
│   │   └── enums/                  # 열거형 정의
│   │       ├── CampsiteStatus.java
│   │       ├── ProductType.java   # RENTAL, SALE
│   │       └── ReservationStatus.java  # CONFIRMED, CANCELLED
│   ├── 📦 dto/                     # 데이터 전송 객체
│   │   ├── CreateRentalRequest.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── ProcessSaleRequest.java
│   │   └── *Response.java (각종 응답 DTO)
│   ├── 🔧 service/                 # 비즈니스 로직 (구현되어야 함)
│   ├── 📊 repository/              # 데이터 접근 계층 (구현되어야 함) 
│   └── ⚙️ config/
│       └── WebConfig.java          # CORS 설정
├── 📁 src/test/java/com/camping/admin/
│   ├── 🥒 CucumberTestRunner.java  # Cucumber 실행 진입점
│   ├── 🛠️ helper/                  # 테스트 헬퍼 클래스
│   │   ├── ApiHelper.java          # API 호출 통합
│   │   ├── HttpMethod.java         # HTTP 메서드 enum
│   │   ├── HttpMethodStrategy.java # 전략 패턴 인터페이스
│   │   ├── *Strategy.java          # HTTP 메서드별 전략 구현
│   │   └── RestAssuredHelper.java  # RestAssured 래퍼
│   └── 📝 steps/                   # Cucumber 스텝 정의
│       ├── Hooks.java              # 테스트 라이프사이클 관리
│       ├── RentalSteps.java        # 대여 관련 스텝
│       ├── ReservationSteps.java   # 예약 관련 스텝
│       ├── StepContext.java        # 테스트 컨텍스트 관리
│       ├── RentalTestFixture.java  # 대여 테스트 픽스처
│       └── ReservationTestFixture.java # 예약 테스트 픽스처
├── 📁 src/test/resources/
│   ├── 🥒 features/               # Gherkin 시나리오 파일
│   │   ├── rental.feature         # 대여 기능 시나리오
│   │   └── reservation.feature    # 예약 기능 시나리오
│   ├── 🗄️ cleanup.sql            # DB 정리 스크립트
│   └── 🗄️ restore-initial-data.sql # 초기 데이터 복원
├── 📁 http/                       # HTTP 요청 테스트 파일
│   ├── auth.http                  # 인증 테스트
│   ├── campsite-admin.http        # 캠프사이트 관리
│   ├── product-admin.http         # 상품 관리
│   ├── rental-admin.http          # 대여 관리
│   ├── reservation-admin.http     # 예약 관리  
│   ├── revenue-admin.http         # 매출 관리
│   └── sales.http                 # 판매 관리
├── 📁 src/main/resources/
│   ├── ⚙️ application.yml        # 애플리케이션 설정
│   └── 🗄️ data.sql              # 초기 데이터
├── 📁 docs/                       # 프로젝트 문서
└── 📄 build.gradle               # 빌드 설정 + DB 관리 Tasks
```

## 🎯 주요 도메인

### 핵심 엔티티 관계

```
Campsite (캠프사이트)
    ↓ 1:N  
Reservation (예약)
    ↓ 1:N
RentalRecord (대여기록) → Product (상품)
    
Product (상품)
    ↓ 1:N
SalesRecord (판매기록)
```

### 비즈니스 규칙

- **상품 타입**: `RENTAL`(대여용), `SALE`(판매용)
- **예약 상태**: `CONFIRMED`(확정), `CANCELLED`(취소)
- **대여 규칙**: RENTAL 타입 상품만 대여 가능
- **재고 관리**: 대여 시 상품 수량 차감

## 🔐 인증 시스템

### JWT 기반 인증

```yaml
# application.yml
jwt:
  secret: "change-this-dev-secret-key-please"
  expiration-minutes: 30

admin:
  username: admin
  password: admin123
```

### 인증 플로우

1. `POST /auth/login` → JWT 토큰 획득
2. `Authorization: Bearer {token}` 헤더로 API 호출
3. 토큰 만료 시 재로그인 필요

## 🗄️ 데이터베이스 설계

### H2 인메모리 DB 설정

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    defer-datasource-initialization: true
  sql:
    init:
      mode: always  # data.sql 자동 실행
```

### 초기 데이터 (data.sql)

- **2개 캠프사이트**: A-01, A-02
- **12개 상품**: 7개 RENTAL, 5개 SALE
- **12개 예약**: 최근 한달 데이터
- **5개 판매기록**: 샘플 매출 데이터

## 📡 API 엔드포인트

### 인증

- `POST /auth/login` - 로그인

### 관리자 기능

- `GET /admin/reservations` - 예약 목록
- `PATCH /admin/reservations/{id}/status` - 예약 상태 변경
- `GET /admin/rentals` - 대여 목록
- `POST /admin/rentals` - 대여 기록 생성
- `GET /admin/products` - 상품 목록
- `POST /admin/sales` - 판매 처리
- `GET /admin/revenue/daily` - 일일 매출
- `GET /admin/revenue/range` - 기간별 매출

## 🔧 설정 특징

### 개발 환경 최적화

- **포트 분리**: 8081 (테스트 서버와 충돌 방지)
- **H2 콘솔 활성화**: 데이터 확인 용이
- **SQL 로깅**: 개발 시 쿼리 확인
- **CORS 허용**: 프론트엔드 개발 지원

### 테스트 환경

- **SpringBootTest 미사용**: 순수 RestAssured
- **외부 서버 의존**: 별도 실행된 Spring 애플리케이션 호출
- **데이터 격리**: Gradle Tasks로 DB 상태 관리

이 아키텍처는 ATDD 방식의 테스트 개발을 위해 최적화되어 있습니다. 🏗️