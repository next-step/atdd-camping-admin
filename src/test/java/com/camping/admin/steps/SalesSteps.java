package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class SalesSteps {

    private TestContext context = TestContext.getInstance();


    // When

    @When("관리자가 판매 목록을 조회한다")
    public void 관리자가_판매_목록을_조회한다() {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .get("/api/sales")
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 판매를 등록한다")
    public void 관리자가_판매를_등록한다() {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .body(Map.of(
                                "items", List.of(
                                        Map.of("productId", 2, "quantity", 2)
                                )
                        ))
                        .when()
                        .post("/api/sales")
                        .then()
                        .extract().response()
        );
    }

    // Then

    @Then("판매 목록이 조회된다")
    public void 판매_목록이_조회된다() {
        context.getLastResponse().then()
                .statusCode(200);
    }

    @Then("판매 등록이 완료된다")
    public void 판매_등록이_완료된다() {
        context.getLastResponse().then()
                .statusCode(200);
    }

}
