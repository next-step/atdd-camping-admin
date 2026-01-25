# ATDD 캠핑 관리자 시스템 - 프로젝트 분석서

> E2E 인수테스트를 위한 프로젝트 참고 문서

---

## 문서 구조

프로젝트 분석 내용은 목적별로 분리되어 있습니다.

| 문서 | 설명 | 용도 |
|------|------|------|
| [API 엔드포인트](docs/API_ENDPOINTS.md) | 전체 API 목록 및 요청/응답 예시 | 테스트 케이스 작성 시 참조 |
| [비즈니스 규칙](docs/BUSINESS_RULES.md) | 핵심 비즈니스 로직 및 검증 규칙 | 테스트 시나리오 설계 시 참조 |
| [이슈 및 개선사항](docs/ISSUES_AND_IMPROVEMENTS.md) | 발견된 버그, 코드 스멜, 개선 방안 | 코드 리뷰 및 리팩토링 참조 |

---

## 프로젝트 개요

### 기술 스택
- **Backend**: Spring Boot
- **Database**: JPA/Hibernate
- **인증**: JWT

### 주요 도메인
- 예약 관리 (Reservation)
- 캠프사이트 관리 (Campsite)
- 상품 관리 (Product)
- 대여 관리 (Rental)
- 판매 (Sales)
- 매출 리포트 (Revenue)

### API 요약
- **관리자 API**: `/admin/**` (JWT 인증 필요)
- **공개 API**: `/api/**`
- **총 20개 엔드포인트**

---

## 빠른 참조

### 상품 타입
| 타입 | 설명 |
|------|------|
| `RENTAL` | 대여 상품 |
| `SALE` | 판매 상품 |

### 예약 상태
| 상태 | 설명 |
|------|------|
| `PENDING` | 대기중 |
| `CONFIRMED` | 확정 |
| `CANCELLED` | 취소 |
| `COMPLETED` | 완료 |

### 매출 계산
- 1박 요금: 50,000원
- 총 매출 = 판매 + 예약 + 대여

---

## 주요 이슈 (P0)

상세 내용은 [이슈 문서](docs/ISSUES_AND_IMPROVEMENTS.md) 참조

1. **재고 감소 Race Condition** - `ProductService.java`
2. **평문 비밀번호** - `AuthController.java`
3. **N+1 쿼리** - `SalesService.java`
