package com.camping.admin.helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

public class ApiHelper {

    public static RequestSpecification given() {
        return RestAssured.given();
    }

    public static RequestSpecification givenAuthenticated() {
        AuthHelper.ensureAuthenticated();
        return RestAssured
                .given()
                .header("Authorization", "Bearer " + AuthHelper.getAccessToken());
    }

    public static RequestSpecification givenAuthenticatedWithJson() {
        return givenAuthenticated()
                .contentType(ContentType.JSON);
    }

    public static Response patch(String path, Object body) {
        return givenAuthenticatedWithJson()
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

    public static void assertStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    public static void assertClientError(Response response) {
        response.then().statusCode(Matchers.greaterThanOrEqualTo(400));
    }

    public static void assertFieldEquals(Response response, String fieldPath, Object expectedValue) {
        response.then().body(fieldPath, Matchers.equalTo(expectedValue));
    }

    public static void assertReservationStatus(Response response, String expectedStatus) {
        assertFieldEquals(response, "status", expectedStatus);
    }

    public static void assertReservationId(Response response, Long expectedId) {
        assertFieldEquals(response, "id", expectedId.intValue());
    }

    public static void assertReservationStatusAndId(Response response, String expectedStatus, Long expectedId) {
        assertStatusCode(response, 200);
        assertReservationStatus(response, expectedStatus);
        assertReservationId(response, expectedId);
    }
}
