package com.camping.admin.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class ApiHelper {

    private static final RestAssuredHelper restAssuredHelper = new RestAssuredHelper();

    public static <T> ExtractableResponse<Response> createExtractableResponse(HttpMethod httpMethod, String url, T body) {
        return restAssuredHelper.execute(httpMethod, url, body);
    }

    public static <T> ExtractableResponse<Response> createExtractableResponseWithAuthorization(HttpMethod httpMethod, String url, T body) {
        return restAssuredHelper.execute(httpMethod, url, body, true);
    }

    public static ExtractableResponse<Response> createExtractableResponse(HttpMethod httpMethod, String url) {
        return restAssuredHelper.execute(httpMethod, url);
    }

    public static ExtractableResponse<Response> createExtractableResponseWithAuthorization(HttpMethod httpMethod, String url) {
        return restAssuredHelper.execute(httpMethod, url, true);
    }

    public static <T> ExtractableResponse<Response> createExtractableResponse(HttpMethod httpMethod, String url, T body, boolean needAuthorization) {
        return restAssuredHelper.execute(httpMethod, url, body, needAuthorization);
    }

}
