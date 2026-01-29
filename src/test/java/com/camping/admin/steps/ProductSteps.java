package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductSteps {

    private TestContext context = TestContext.getInstance();

    // When
    @When("관리자가 상품 목록을 조회한다")
    public void 관리자가_상품_목록을_조회한다() {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .get("/admin/products")
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 상품을 등록한다")
    public void 관리자가_상품을_등록한다() {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .body(Map.of(
                                "name", "테스트 상품",
                                "stockQuantity", 10,
                                "price", 5000,
                                "productType", "SALE"
                        ))
                        .when()
                        .post("/admin/products")
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 등록된 상품 {int}을 수정한다")
    public void 관리자가_등록된_상품을_수정한다(int id) {
        given()
                .spec(context.getAuthHelper().authorizedSpec())
                .when()
                .get("/admin/products")
                .then()
                .statusCode(200)
                .body("find { it.id == " + id + " }", notNullValue());


        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .body(Map.of("name", "수정된 상품명"))
                        .when()
                        .put("/admin/products/" + id)
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 존재하지 않는 상품 {int}을 수정한다")
    public void 관리자가_존재하지_않는_상품을_수정한다(int id) {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .body(Map.of("name", "수정 시도"))
                        .when()
                        .put("/admin/products/" + id)
                        .then()
                        .extract().response()
        );
    }

    // Then

    @Then("상품 목록이 조회된다")
    public void 상품_목록이_조회된다() {
        context.getLastResponse().then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Then("상품 등록이 완료된다")
    public void 상품_등록이_완료된다() {
        context.getLastResponse().then()
                .statusCode(201)
                .body("name", equalTo("테스트 상품"));
    }

    @Then("상품 수정이 완료된다")
    public void 상품_수정이_완료된다() {
        context.getLastResponse().then()
                .statusCode(200)
                .body("name", equalTo("수정된 상품명"));
    }

}
