package com.camping.admin;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CommonContext {
    public static RequestSpecification baseSpec;
    public static RequestSpecification requestSpec;
    public static String accessToken;
    public static Long reservationId;
    public static Long invalidReservationId;
    public static ExtractableResponse<Response> lastResponse;
}
