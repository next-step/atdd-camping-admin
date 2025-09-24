package com.camping.admin.steps;

import static com.camping.admin.support.CommonContext.lastParams;
import static org.assertj.core.api.Assertions.*;
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
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationStepDefs {

    private Long reservationId;

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        // data.sql 기반 픽스처 세팅
        reservationId = 1L;
        lastParams.put("status", "CONFIRMED");

        // 여러 테스트에서 사용해도 해당 예약 상태가 동일하도록 초기화 수행
        String url = String.format("/admin/reservations/%d/status", reservationId);
        Map<String, Object> body = Map.of("status", "CONFIRMED");

        ApiHelper.request(HttpMethod.PATCH, url, body)
                .then().statusCode(200);
    }

    @Then("예약은 {string} 상태다")
    public void 예약은상태다(String statusValue) {
        String url = "/admin/reservations";

        ExtractableResponse<Response> reservationsResponse = ApiHelper.request(HttpMethod.GET, url, null)
                .then().statusCode(200)
                .extract();

        String status = reservationsResponse.jsonPath().getString(String.format("find {it.id == %d}.status", reservationId));
        assertNotNull(status, "취소 예약 대상을 조회하지 못했습니다.");
        assertThat(status).isEqualTo(statusValue);
    }

    @When("관리자가 예약을 {string} 상태로 변경한다")
    public void 관리자가예약을상태로변경한다(String statusValue) {
        Map<String, Object> body = Map.of("status", statusValue);
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

    @When("관리자가 변경 내용 없이 사용자 예약 상태 변경을 시도한다")
    public void 관리자가변경내용없이사용자예약상태변경을시도한다() {
        String url = String.format("/admin/reservations/%d/status", reservationId);

        CommonContext.lastResponse = ApiHelper.request(HttpMethod.PATCH, url, Map.of());
    }

    @Then("잘못된 요청 형식으로 인해 예약 상태 변경이 실패한다")
    public void 잘못된요청형식으로인해예약상태변경이실패한다() {
        CommonContext.lastResponse.then().statusCode(400);
    }

    @And("응답에 기존 사용자 예약 정보가 포함된다")
    public void 응답에기존사용자예약정보가포함된다() {
        JsonPath jsonPath = CommonContext.lastResponse.then().extract().jsonPath();
        Long extractedId = jsonPath.getLong("id");
        String status = jsonPath.getString("status");

        assertThat(extractedId).isEqualTo(reservationId);
        assertThat(status).isEqualTo(CommonContext.lastParams.get("status"));
    }
}
