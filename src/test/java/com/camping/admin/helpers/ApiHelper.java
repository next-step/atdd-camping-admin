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

    public static Response get(String path) {
        return givenAuthenticated()
                .when()
                .get(path);
    }

    public static Response post(String path, Object body) {
        return givenAuthenticatedWithJson()
                .body(body)
                .when()
                .post(path);
    }

    public static Response put(String path, Object body) {
        return givenAuthenticatedWithJson()
                .body(body)
                .when()
                .put(path);
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

    public static Response delete(String path) {
        return givenAuthenticated()
                .when()
                .delete(path);
    }

    public static Response getWithExpectedStatus(String path, int expectedStatus) {
        return get(path)
                .then()
                .statusCode(expectedStatus)
                .extract().response();
    }

    public static Response postWithExpectedStatus(String path, Object body, int expectedStatus) {
        return post(path, body)
                .then()
                .statusCode(expectedStatus)
                .extract().response();
    }

    public static Response patchWithExpectedStatus(String path, Object body, int expectedStatus) {
        return patch(path, body)
                .then()
                .statusCode(expectedStatus)
                .extract().response();
    }

    public static void assertStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    public static void assertSuccessfulStatusCode(Response response) {
        response.then().statusCode(Matchers.allOf(
            Matchers.greaterThanOrEqualTo(200),
            Matchers.lessThan(300)
        ));
    }

    public static void assertClientError(Response response) {
        response.then().statusCode(Matchers.greaterThanOrEqualTo(400));
    }

    public static void assertFieldEquals(Response response, String fieldPath, Object expectedValue) {
        response.then().body(fieldPath, Matchers.equalTo(expectedValue));
    }

    public static void assertFieldNotNull(Response response, String fieldPath) {
        response.then().body(fieldPath, Matchers.notNullValue());
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

    public static void assertListNotEmpty(Response response) {
        response.then().body("size()", Matchers.greaterThan(0));
    }

    public static void assertListSize(Response response, int expectedSize) {
        response.then().body("size()", Matchers.equalTo(expectedSize));
    }
}
