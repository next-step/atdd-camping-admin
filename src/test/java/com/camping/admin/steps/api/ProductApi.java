package com.camping.admin.steps.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NonAsciiCharacters")
@Component
public class ProductApi {

    public ExtractableResponse<Response> 상품_생성_요청(String accessToken, String name, int stockQuantity, int price, String productType) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("stockQuantity", stockQuantity);
        params.put("price", price);
        params.put("productType", productType);

        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/products")
                .then().log().all()
                .extract();
    }
    public ExtractableResponse<Response> 상품_목록_조회_요청(String accessToken) {
        return 상품_목록_조회_요청(accessToken, null);
    }

    public ExtractableResponse<Response> 상품_목록_조회_요청(String accessToken, String query) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("query", query)
                .when().get("/admin/products")
                .then().log().all()
                .extract();
    }
}
