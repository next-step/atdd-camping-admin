package com.camping.admin.helpers;

import com.camping.admin.fixtures.ScenarioContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ApiClientBase {

    @Autowired
    protected ScenarioContext context;

    protected RequestSpecification authenticated() {
        return RestAssured.given()
            .header("Authorization", "Bearer " + context.adminToken)
            .contentType(ContentType.JSON);
    }

    protected RequestSpecification unauthenticated() {
        return RestAssured.given()
            .contentType(ContentType.JSON);
    }
}
