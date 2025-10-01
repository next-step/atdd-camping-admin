package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import com.camping.admin.support.RequestSpecFactory;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ReservationListSteps {
    public static Response lastResponse;

    @When("관리자가 예약 목록을 조회했다.")
    public void 관리자가예약목록을조회했다() {
        lastResponse = given().spec(RequestSpecFactory.create())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/reservations");
    }
}
