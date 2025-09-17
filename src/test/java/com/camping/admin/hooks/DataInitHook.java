package com.camping.admin.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.AfterAll;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class DataInitHook {
    @Before
    public void setupTestData() {
        // 새로운 데이터 초기화 (data.sql 실행)
        RestAssured.given()
                .when().post("/test-api/data/init")
                .then().statusCode(200);

        System.out.println("테스트 데이터 초기화 완료");
    }

    @AfterAll
    public static void cleanupSuite() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when().delete("/test-api/data/cleanup")
                .then().statusCode(200);

        System.out.println("최종 데이터 정리 완료");
    }
}
