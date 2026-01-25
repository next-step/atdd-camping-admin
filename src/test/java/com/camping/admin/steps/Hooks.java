package com.camping.admin.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class Hooks {

    @Before(order = 0)
    public void setupRestAssuredBase() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        System.out.println(">>> [Before Hook] RestAssured 기본 설정 완료.");
    }

    @After
    public void cleanup() {
        System.out.println(">>> [After Hook] 시나리오 종료 후 정리 작업 수행.");
    }
}
