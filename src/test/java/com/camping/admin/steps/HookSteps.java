package com.camping.admin.steps;

import com.camping.admin.support.DatabaseCleaner;
import com.camping.admin.support.TestContext;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public class HookSteps {

    @Autowired private TestContext context;

    @Autowired
    @Qualifier("deleteAndReset")
    private DatabaseCleaner databaseCleaner;

    @Before
    public void beforeScenario() {
        // DB 초기화
        databaseCleaner.clean();

        // 컨텍스트 초기화
        context.reset();

        // JWT 토큰 발급
        context.jwtToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
                .post("/auth/login")
                .jsonPath()
                .getString("accessToken");
    }
}
