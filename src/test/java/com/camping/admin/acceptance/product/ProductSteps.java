package com.camping.admin.acceptance.product;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class ProductSteps {
    private Long productId;
    private String adminToken;
    ExtractableResponse<Response> response;

    @Given("관리자가 상품 판매를 시작했다")
    public void 관리자가_상품판매를_시작했다() {
        productId = 2L;

        adminToken = RestAssured
                .given()
                    .contentType("application/json")
                    .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                    .post("/auth/login")
                .then().log().all()
                .extract()
                    .cookie("AUTH_TOKEN");

        response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType("application/json")
                .when()
                    .get("/admin/products")
                .then().log().all()
                .extract();
        String productType = String.valueOf(response.jsonPath().getMap(String.format("find { it.id == %d }", productId)).get("productType"));
        assertThat(productType.equals("SALE"));
    }

    @When("관리자가 판매방식을 {string}로 변경했다")
    public void 관리자가_판매방식을_변경했다(String productType) {
        adminToken = RestAssured
                .given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");

        Map<String, String> request = Map.of("productType", productType);
        response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType("application/json")
                .body(request)
                .when()
                    .put("/admin/products/{id}", productId)
                .then().log().all()
                .statusCode(200)
                .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Then("상품을 대여할 수 있게 된다")
    public void 상품을_대여할_수_있게된다() {
        String productType = response.jsonPath().getString("productType");
        assertThat(productType.equals("RENTAL"));
    }
}