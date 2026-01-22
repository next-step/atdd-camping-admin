package com.camping.admin.steps;


import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Cucumber + Spring Boot 통합 설정
 *
 * @CucumberContextConfiguration: Cucumber가 Spring 컨텍스트를 사용하도록 설정
 * @SpringBootTest(RANDOM_PORT): 실제 서버를 랜덤 포트로 띄워서 HTTP 요청 테스트 가능
 * @ActiveProfiles("test"): application-test.yml 설정 사용 (data.sql 비활성화)
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
