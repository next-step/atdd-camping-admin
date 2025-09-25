package com.camping.admin.hooks;

import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class ScenarioHooks {
    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
        // TODO: 데이터베이스 초기화
    }
}
