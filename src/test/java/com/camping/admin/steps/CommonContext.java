package com.camping.admin.steps;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class CommonContext {
    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 8080;

    private RequestSpecification requestSpec;

    public void login(String username, String password) {
        String token = RestAssured.given()
            .baseUri(BASE_URI)
            .port(PORT)
            .contentType(ContentType.JSON)
            .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("accessToken");

        requestSpec = RestAssured.given()
            .baseUri(BASE_URI)
            .port(PORT)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token);
    }

    public String getBaseUri() {
        return BASE_URI;
    }

    public int getPort() {
        return PORT;
    }

    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }
}