package com.camping.admin.apiExtractableresponse;

import com.camping.admin.steps.CommonHooks;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class ProductApiExtractableResponse {

    public static Response 상품을_등록한다(Map<String, Object> productData) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body(productData)
                .when()
                .post("/admin/products");
    }

    public static Response 상품_목록을_조회한다() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/admin/products");
    }
}