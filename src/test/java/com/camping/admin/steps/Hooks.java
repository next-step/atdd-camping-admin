package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import com.camping.admin.support.RequestSpecFactory;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class Hooks {
    
    @BeforeAll
    public static void initTokens() {
        RequestSpecification spec = RequestSpecFactory.create();
        
        // Get admin token
        String adminToken = given().spec(spec)
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then().extract()
                .cookie("AUTH_TOKEN");
        
        CommonContext.setAdminToken(adminToken);
    }
    
    @Before
    public void beforeScenario() {
        CommonContext.setRequestSpec(RequestSpecFactory.create());
    }
}
