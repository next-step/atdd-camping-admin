package com.camping.admin.steps;

import com.camping.admin.support.ApiHelper;
import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.Matchers.*;

public class ReservationListStepDefs {

    @Given("예약 데이터가 존재한다")
    public void 예약데이터가존재한다() {
        // data.sql에 이미 예약 데이터가 존재하므로 별도 처리 불필요
    }

    @When("관리자가 예약 목록을 조회한다")
    public void 관리자가예약목록을조회한다() {
        ApiHelper.makeAuthenticatedRequest("GET", "/admin/reservations", null);
    }

    @Then("예약 목록이 반환된다")
    public void 예약목록이반환된다() {
        CommonContext.getLastResponse().then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("예약 정보에는 고객명과 캠프사이트 정보가 포함된다")
    public void 예약정보에는고객명과캠프사이트정보가포함된다() {
        CommonContext.getLastResponse().then()
                .body("[0].customerName", notNullValue())
                .body("[0].campsiteSiteNumber", notNullValue());
    }

    @And("빈 예약 목록이 반환된다")
    public void 빈예약목록이반환된다() {
        CommonContext.getLastResponse().then()
                .body("size()", equalTo(0));
    }

    @When("권한 없는 사용자가 예약 목록을 조회한다")
    public void 권한없는사용자가예약목록을조회한다() {
        ApiHelper.makeUnauthenticatedRequest("GET", "/admin/reservations", null);
    }
}
