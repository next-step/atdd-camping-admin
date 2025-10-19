package com.camping.admin.steps;

import com.camping.admin.support.HttpSupport;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class ReservationListSteps {
    public static Response lastResponse;

    @When("관리자가 예약 목록을 조회했다.")
    public void 관리자가예약목록을조회했다() {
        lastResponse = HttpSupport.getWithAuth("/admin/reservations");
    }
}
