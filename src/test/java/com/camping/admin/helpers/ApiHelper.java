package com.camping.admin.helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

public class ApiHelper {

    public static RequestSpecification givenAuthenticated() {
        AuthHelper.ensureAuthenticated();
        return RestAssured
                .given()
                .header("Authorization", "Bearer " + AuthHelper.getAccessToken());
    }

    public static Response patch(String path, Object body) {
        return givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch(path);
    }

    public static Response patch(String path) {
        return givenAuthenticated()
                .contentType(ContentType.JSON)
                .when()
                .patch(path);
    }

    public static void assertClientError(Response response) {
        response.then().statusCode(Matchers.greaterThanOrEqualTo(400));
    }

    public static void assertReservationStatusAndId(Response response, String expectedStatus, Long expectedId) {
        response.then().statusCode(200);
        response.then().body("status", Matchers.equalTo(expectedStatus));
        response.then().body("id", Matchers.equalTo(expectedId.intValue()));
    }
}
