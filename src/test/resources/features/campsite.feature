Feature: 캠프사이트 관리

  Scenario: 관리자가 캠프사이트를 성공적으로 등록한다
    When 관리자가 사이트 번호 "A10", 설명 "호수뷰 사이트", 최대 인원 4인 캠프사이트를 등록하면
    Then 캠프사이트가 성공적으로 등록된다
    And 등록된 캠프사이트의 번호는 "A10", 설명은 "호수뷰 사이트", 최대 인원은 4이어야 한다

  Scenario: 중복된 사이트 번호로 캠프사이트 등록 시 실패한다
    Given "B1" 사이트 번호를 가진 캠프사이트가 이미 등록되어 있다
    When 관리자가 사이트 번호 "B1", 설명 "중복 사이트", 최대 인원 2인 캠프사이트를 등록하면
    Then 캠프사이트 등록이 실패한다

  Scenario Outline: 필수 값이 유효하지 않으면 캠프사이트 등록에 실패한다
    When 관리자가 사이트 번호 <siteNumber>, 설명 <description>, 최대 인원 <maxPeople>인 캠프사이트를 등록하면
    Then 캠프사이트 등록이 실패한다

    Examples:
      | siteNumber | description | maxPeople |
      | ""         | "설명"      | 4         |
      | "null"     | "설명"      | 4         |