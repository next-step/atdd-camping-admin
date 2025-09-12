# 인수 테스트 규칙

## 네이밍 규칙
1. Given, When 스텝은 ~한다. 로 끝난다.
2. Then 스텝은 ~된다. 로 끝난다.

## 스텝 규칙
1. Given, When 스텝은 response 를 TestContext 에 저장한다.
2. Then 스텝은 TestContext 에 저장된 response 를 사용해 검증한다.
3. 생성 스텝은 해당 데이터의 id 값을 TestContext 에 저장한다.
4. 조회, 수정, 삭제 스텝은 TestContext 에 저장된 id 값을 사용한다.
5. ResponseSteps 는 statusCode, 에러 메시지 등 모든 feature 에서 검증할 수 있는 스텝만 작성한다.
