package com.camping.admin.helper;

import io.restassured.response.Response;

import static org.springframework.http.HttpStatus.*;

public class ResponseValidator {

    public static void isOk(Response response) {
        response.then()
                .statusCode(OK.value());
    }

    public static void isBadRequest(Response response) {
        response.then()
                .statusCode(BAD_REQUEST.value());
    }

    public static void isCreated(Response response) {
        response.then()
                .statusCode(CREATED.value());
    }

    public static void isConflict(Response response) {
        response.then()
                .statusCode(CONFLICT.value());
    }
}
