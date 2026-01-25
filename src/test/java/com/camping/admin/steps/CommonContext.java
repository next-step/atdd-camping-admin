package com.camping.admin.steps;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Cucumber 시나리오 간 공유되는 인증/세션 컨텍스트
 * - 인증 처리 및 RequestSpec 관리만 담당
 * - 도메인별 상태는 각 Helper 클래스에서 관리
 */
public class CommonContext {
    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 8080;

    private RequestSpecification requestSpec;
    private Response lastResponse;

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

    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }

    // Shared state for product-related steps
    private int targetProductId;
    private int originalStock;

    public int getTargetProductId() {
        return targetProductId;
    }

    public void setTargetProductId(int targetProductId) {
        this.targetProductId = targetProductId;
    }

    public int getOriginalStock() {
        return originalStock;
    }

    public void setOriginalStock(int originalStock) {
        this.originalStock = originalStock;
    }
}