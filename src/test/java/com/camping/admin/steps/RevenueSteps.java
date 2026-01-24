package com.camping.admin.steps;

import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RevenueSteps {

    private final SharedState state;

    public RevenueSteps(SharedState state) {
        this.state = state;
    }

    // === When ===

    @When("일별 매출을 조회하면")
    public void 일별_매출을_조회하면() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .queryParam("date", today)
                .when()
                .get("/admin/reports/revenue/daily"));
    }

    @When("월별 매출을 조회하면")
    public void 월별_매출을_조회하면() {
        LocalDate now = LocalDate.now();
        String from = now.withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE);
        String to = now.format(DateTimeFormatter.ISO_DATE);
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("/admin/reports/revenue/range"));
    }

    @When("기간별 매출을 조회하면")
    public void 기간별_매출을_조회하면() {
        String from = LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_DATE);
        String to = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("/admin/reports/revenue/range"));
    }
}
