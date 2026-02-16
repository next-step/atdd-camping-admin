Feature: 캠프사이트 관리
  캠핑장 관리자는 캠프사이트를 조회, 등록, 수정할 수 있다.

  Scenario: 캠프사이트 목록을 조회한다
    Given 다음 캠프사이트가 등록되어 있다
      | siteNumber | description | maxPeople |
      | A-01       | 숲 뷰, 전기가능  | 4         |
      | A-02       | 강가, 그늘많음   | 6         |
    When 캠프사이트 목록을 조회 요청하면 (GET "/admin/campsites")
    Then 요청이 성공한다 (200)
    And 응답 목록의 크기는 2이다

  Scenario: 새로운 캠프사이트를 등록한다
    When 다음 정보로 캠프사이트 등록 요청을 보내면 (POST "/admin/campsites")
      | siteNumber | description    | maxPeople |
      | B-01       | 호수 뷰, 넓은 공간 | 5         |
    Then 리소스가 생성된다 (201)
    And 응답의 "siteNumber" 값은 "B-01"이다
    And 응답의 "maxPeople" 값은 5이다

  Scenario: 캠프사이트 정보를 수정한다
    Given 사이트 번호 "C-01", 설명 "기본 사이트", 최대 인원 4인 캠프사이트가 등록되어 있다
    When 해당 캠프사이트에 다음 정보로 수정 요청을 보내면 (PUT "/admin/campsites/{id}")
      | description          | maxPeople |
      | 리뉴얼 완료, 전기/수도 가능 | 6         |
    Then 요청이 성공한다 (200)
    And 응답의 "description" 값은 "리뉴얼 완료, 전기/수도 가능"이다
    And 응답의 "maxPeople" 값은 6이다
