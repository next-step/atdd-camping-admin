package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;

public class RevenueSteps {

    private TestContext context = TestContext.getInstance();


    // When

    @When("관리자가 오늘 매출을 조회한다")
    public void 관리자가_오늘_매출을_조회한다() {
        String today = LocalDate.now().toString();
        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .queryParam("date", today)
                .when()
                .get("/admin/reports/revenue/daily")
                .then()
                .extract().response()
        );
    }

    @When("관리자가 이번달 매출을 조회한다")
    public void 관리자가_이번달_매출을_조회한다() {
        LocalDate now = LocalDate.now();
        String from = now.withDayOfMonth(1).toString();
        String to = now.toString();

        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("/admin/reports/revenue/daily")
                .then()
                .extract().response()
        );
    }


    // Then

    @Then("일별 매출이 조회된다")
    public void 일별_매출이_조회된다() {
        context.getLastResponse().then()
                .statusCode(200);
    }

    @Then("기간별 매출이 조회된다")
    public void 기간별_매출이_조회된다() {
        context.getLastResponse().then()
                .statusCode(200);
    }
}
