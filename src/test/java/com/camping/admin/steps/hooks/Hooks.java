package com.camping.admin.steps.hooks;

import static io.restassured.RestAssured.given;

import com.camping.admin.config.RequestSpecFactory;
import com.camping.admin.context.CommonContext;
import io.cucumber.java.Before;
import java.util.Map;

public class Hooks {
    @Before
    public void setUp() {
        CommonContext.setRequestSpec(RequestSpecFactory.create());
        authenticate();
        cleanupDatabase();
    }

    private void authenticate() {
        String adminToken = given()
                .spec(CommonContext.getRequestSpec())
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("AUTH_TOKEN");

        CommonContext.setAdminToken(adminToken);
    }

    private void cleanupDatabase() {
        given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .when()
                .post("/api/admin/reset-db")
                .then()
                .statusCode(200);
    }


}
