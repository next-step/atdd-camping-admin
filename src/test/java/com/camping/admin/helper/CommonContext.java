package com.camping.admin.helper;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * 테스트 전역 공용 test fixture
 */
public class CommonContext {

    public static String adminToken;
    public static RequestSpecification requestSpec;
    public static Response lastResponse;
}
