Feature: 예약 상태 업데이트

# TODO: parameterizedTest 적용 필요
  Scenario Outline: 예약 상태를 성공적으로 업데이트한다
    Given 예약이 존재한다
    When 관리자가 예약 상태를 '<status>'로 변경한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 '<status>'로 변경되었다

    Examples:
      | status      |
      | WAITING     |
      | PENDING     |
      | CONFIRMED   |
      | REJECTED    |
      | CHECKED_IN  |
      | CHECKED_OUT |
      | CANCELLED   |

  Scenario: 존재하지 않는 예약의 상태 업데이트를 시도하면 실패한다.
    When 예약 ID 999는 존재하지 않는다
    And 관리자가 예약 상태를 변경한다
    Then 예약 상태 업데이트가 실패한다

  Scenario: 빈 요청 본문으로 상태 업데이트를 시도하면 실패한다.
    Given 예약이 존재한다
    When 관리자가 본문 없이 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

  ## TODO 업데이트 실패해야함.
  Scenario: 상태값이 없는 요청으로 업데이트를 시도하면 기존 상태를 유지한다.
    Given 예약이 존재한다
    And 예약의 현재 상태가 'CONFIRMED'이다
    When 관리자가 상태값 없이 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CONFIRMED'로 유지되었다

    ## TODO 업데이트 실패해야함.
  Scenario: 빈 문자열 상태값으로 업데이트를 시도하면 기존 상태를 유지한다.
    Given 예약이 존재한다
    And 예약의 현재 상태가 'CONFIRMED'이다
    When 관리자가 빈 문자열 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CONFIRMED'로 유지되었다

  Scenario: null 예약 ID로 상태 업데이트를 시도하면 실패한다.
    When 관리자가 null 예약 ID로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

  ## TODO: 겹침 with 존재하지 않는 예약의 상태 업데이트를 시도하면 실패한다.
  Scenario: 음수 예약 ID로 상태 업데이트를 시도하면 실패한다.
    When 관리자가 예약 ID -1로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

    ## TODO: 겹침 with 존재하지 않는 예약의 상태 업데이트를 시도하면 실패한다.
  Scenario: 0 예약 ID로 상태 업데이트를 시도하면 실패한다.
    When 관리자가 예약 ID 0으로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

    ## TODO: 겹침 with 존재하지 않는 예약의 상태 업데이트를 시도하면 실패한다.
  Scenario: 매우 큰 예약 ID로 상태 업데이트를 시도하면 실패한다.
    When 관리자가 예약 ID 9999999999로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다

    ## TODO: 겹침 with 빈 요청 본문으로 상태 업데이트를 시도하면 실패한다.
  Scenario: null 요청 본문으로 상태 업데이트를 시도하면 실패한다.
    Given 예약이 존재한다
    When 관리자가 null 요청 본문으로 상태 업데이트를 시도한다
    Then 예약 상태 업데이트가 실패한다


     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 공백만 있는 상태값으로 업데이트를 시도하면 기존 상태를 유지한다.
    Given 예약이 존재한다
    And 예약의 현재 상태가 'CONFIRMED'이다
    When 관리자가 공백만 있는 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CONFIRMED'로 유지되었다


     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 탭 문자만 있는 상태값으로 업데이트를 시도하면 기존 상태를 유지한다.
    Given 예약이 존재한다
    And 예약의 현재 상태가 'CONFIRMED'이다
    When 관리자가 탭 문자만 있는 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CONFIRMED'로 유지되었다


     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 개행 문자만 있는 상태값으로 업데이트를 시도하면 기존 상태를 유지한다.
    Given 예약이 존재한다
    And 예약의 현재 상태가 'CONFIRMED'이다
    When 관리자가 개행 문자만 있는 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CONFIRMED'로 유지되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: status 키가 존재하지만 값이 null인 경우 기존 상태를 유지한다.
    Given 예약이 존재한다
    And 예약의 현재 상태가 'CONFIRMED'이다
    When 관리자가 null 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CONFIRMED'로 유지되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 숫자 타입의 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 숫자 123을 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 '123'으로 변경되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: boolean 타입의 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 boolean true를 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'true'로 변경되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 배열 타입의 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 배열을 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 배열의 문자열 표현으로 변경되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 객체 타입의 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 객체를 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 객체의 문자열 표현으로 변경되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 매우 긴 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 매우 긴 문자열을 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 해당 긴 문자열로 변경되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 특수 문자가 포함된 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 특수 문자가 포함된 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 특수 문자가 포함된 값으로 변경되었다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 유니코드 문자가 포함된 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 유니코드 문자가 포함된 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 유니코드 문자가 포함된 값으로 변경되었다

     ## TODO: 지워야하는 테스트
  Scenario: 여러 키가 있는 요청에서 status만 처리한다.
    Given 예약이 존재한다
    When 관리자가 여러 키가 포함된 요청으로 상태를 'CANCELLED'로 변경한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'CANCELLED'로 변경되었다
    And 다른 필드는 변경되지 않았다

     ## TODO: 유효하지 않은 값 : 실패
  Scenario: 대소문자가 섞인 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 예약 상태를 'ChEcKeD_iN'으로 변경한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 'ChEcKeD_iN'으로 변경되었다

  ## TODO: 유효하지 않은 값 : 실패
  Scenario: 앞뒤 공백이 있는 상태값으로 업데이트한다.
    Given 예약이 존재한다
    When 관리자가 앞뒤 공백이 있는 상태값으로 업데이트를 시도한다
    Then 예약 상태 업데이트에 성공했다
    And 예약 상태가 앞뒤 공백이 포함된 값으로 변경되었다
