package com.camping.admin.steps;

import com.camping.admin.support.DatabaseCleaner;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    private int port;

    /**
     * .   이 Hook은 3개 Feature 파일의 모든 시나리오에서 자동 실행됩니다.
     *   - rental_create.feature의 시나리오 10개 → 10번 실행
     *   - reservation_cancel.feature의 시나리오 5개 → 5번 실행
     */
    @Before
    public void setUp() {
        RestAssured.port = port;
        databaseCleaner.clear();
    }
}
