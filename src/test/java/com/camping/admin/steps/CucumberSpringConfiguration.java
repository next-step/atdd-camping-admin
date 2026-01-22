package com.camping.admin.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration // Cucumber와 Spring Context 연동을 위한 애노테이션
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 랜덤 포트에 실제 웹 서버를 띄웁니다.
public class CucumberSpringConfiguration {
}
