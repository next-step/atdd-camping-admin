package com.camping.admin.steps;

import static io.restassured.RestAssured.given;

import com.camping.admin.support.CommonContext;
import com.camping.admin.support.RequestSpecFactory;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class Hooks {

    @BeforeAll
    public static void beforeAll() {
        RequestSpecification spec = RequestSpecFactory.create();
        CommonContext.adminToken = given().spec(spec)
                .body(Map.of("username","admin","password","admin123"))
                .post("/auth/login").then().extract().cookie("AUTH_TOKEN");
    }

    @Before
    public void beforeScenario() {
        CommonContext.requestSpec = RequestSpecFactory.create();
        CommonContext.lastParams = new HashMap<>();
    }

}
