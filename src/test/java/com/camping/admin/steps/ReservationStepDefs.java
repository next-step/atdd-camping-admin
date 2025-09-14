package com.camping.admin.steps;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.camping.admin.support.ApiHelper;
import com.camping.admin.support.CommonContext;
import io.cucumber.core.options.CurlOption.HttpMethod;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationStepDefs {

    private Long reservationId;

    @Given("관리자가 로그인했다")
    public void 관리자가로그인했다() {
        CommonContext.adminToken = given().spec(CommonContext.requestSpec)
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");
    }

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        reservationId = 1L;

        // 여러 테스트에서 사용해도 해당 예약 상태가 동일하도록 초기화 수행
        String url = String.format("/admin/reservations/%d/status", reservationId);
        Map<String, Object> body = Map.of("status", "CONFIRMED");

        ApiHelper.request(HttpMethod.PATCH, url, body)
                .then().statusCode(200);
    }

    @When("관리자가 예약을 취소했다")
    public void 관리자가예약을취소했다() {
        String url = String.format("/admin/reservations/%d/status", reservationId);
        Map<String, Object> body = Map.of("status", "CANCELLED");

        ApiHelper.request(HttpMethod.PATCH, url, body);
    }

    @Then("예약은 취소 상태다")
    public void 예약은취소상태다() {
        String url = "/admin/reservations";

        ExtractableResponse<Response> reservationsResponse = ApiHelper.request(HttpMethod.GET, url, null)
                .then().statusCode(200)
                .extract();

        String status = reservationsResponse.jsonPath().getString(String.format("find {it.id == %d}.status", reservationId));
        assertNotNull(status, "취소 예약 대상을 조회하지 못했습니다.");
        assertThat(status).isEqualTo("CANCELLED");
    }

    @Then("예약은 체크인 상태다")
    public void 예약은체크인상태다() {
        String url = "/admin/reservations";

        ExtractableResponse<Response> reservationsResponse = ApiHelper.request(HttpMethod.GET, url, null)
                .then().statusCode(200)
                .extract();

        String status = reservationsResponse.jsonPath().getString(String.format("find {it.id == %d}.status", reservationId));
        assertNotNull(status, "취소 예약 대상을 조회하지 못했습니다.");
        assertThat(status).isEqualTo("CHECKED_IN");
    }

    @When("관리자가 예약을 체크인 상태로 변경한다")
    public void 관리자가예약을체크인상태로변경한다() {
        Map<String, Object> body = Map.of("status", "CHECKED_IN");
        String url = String.format("/admin/reservations/%d/status", reservationId);

        CommonContext.lastResponse = ApiHelper.request(HttpMethod.PATCH, url, body);
    }

    @And("예약 상태 변경이 실패한다")
    public void 예약상태변경이실패한다() {
        CommonContext.lastResponse.then().statusCode(allOf(greaterThanOrEqualTo(400), lessThan(600)));
    }

    @Then("변경 요청이 성공한다")
    public void 변경요청이성공한다() {
        CommonContext.lastResponse.then().statusCode(200);
    }
}
