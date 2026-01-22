package com.camping.admin.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class Books {
    @Before(order = 0)
    public void setupRestAssuredBase()
    {
        // Pure Black-box 환경에서는 정적인 설정
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @After
    public void cleanup() {
        // 시나리오 종료 후 필요한 정리 작업을 수행합니다.
    }
}
