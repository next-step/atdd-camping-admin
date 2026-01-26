Feature: 예약 상태 변경 예외 및 엣지 케이스 처리

  Background:
    Given 캠핑장에 'A-01' 사이트가 등록되어 있다
    And 관리자 로그인이 되어 있다

  Scenario: 존재하지 않는 예약의 상태를 변경하려고 시도한다
    When 관리자가 존재하지 않는 예약(ID 9999)의 상태를 확정하려고 하면
    Then 요청이 실패한다(500)

  Scenario Outline: 잘못된 요청 본문으로 예약 상태 변경을 시도한다
    Given 사이트 번호가 'A-01'인 캠핑장에 '이순신'이 대기 상태로 예약되어 있다
    When 관리자가 잘못된 본문('<request_body>')으로 예약 상태 변경을 요청하면
    Then 요청이 실패한다(400)

    Examples:
         | request_body            | 설명                   |
         | {}                      | 비어있는 JSON          |
         | {"state": "CHECKED_IN"} | 유효하지 않은 상태값   |
         | {"status": null}        | 필드 값으로 null       |
         | {"status": ""}          | 필드 값으로 빈 문자열  |

  Scenario: 이미 취소된 예약의 상태를 변경하려고 시도한다
    Given 사이트 번호가 'A-01'인 캠핑장에 '홍길동' 이름으로 취소 상태의 예약이 있다
    When 관리자가 예약을 확정하면
    Then 요청이 실패한다(409)
