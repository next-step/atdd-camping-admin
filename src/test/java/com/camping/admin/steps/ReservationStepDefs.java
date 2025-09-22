package com.camping.admin.steps;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.support.api.AuthApi;
import com.camping.admin.support.api.ReservationApi;
import com.camping.admin.support.context.ReservationWorld;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class ReservationStepDefs {

    private final ReservationWorld world;
    private final AuthApi authApi = new AuthApi();
    private final ReservationApi reservationApi = new ReservationApi();

    public ReservationStepDefs(ReservationWorld world) {
        this.world = world;
    }

    @Given("관리자가 로그인을 하였다.")
    public void 관리자가로그인을하였다() {
        world.authToken = authApi.loginAndGetCookieToken("admin", "admin123");
    }

    @Given("WATING 상태인 예약이 존재한다.")
    public void wating상태인예약이존재한다() {
        world.reservationId = 13L;
    }

    @When("관리자가 WATING 상태인 예약 상태를 PENDING 상태로 변경한다.")
    public void 관리자가WATING상태인예약상태를PENDING상태로변경한다() {
        var response = reservationApi.patchStatus(world.authToken, world.reservationId, "PENDING");
        ReservationResponse reservationResponse = response.as(ReservationResponse.class);
        world.status = reservationResponse.getStatus();
    }

    @Then("예약 상태가 PENDING 으로 변경된다.")
    public void 예약상태가PENDING으로변경된다() {
        assertThat(world.status).isEqualTo("PENDING");
    }

    @Given("존재하지 않는 예약 ID 9999 가 있다.")
    public void 존재하지않는예약ID가있다() {
        world.reservationId = 9999L;
    }

    @When("관리자가 예약 ID 9999 의 상태를 PENDING 상태로 변경한다.")
    public void 관리자가예약ID의상태를PENDING상태로변경한다() {
        world.response = reservationApi.patchStatus(world.authToken, world.reservationId, "PENDING");
    }

    @Then("에러 응답이 발생한다.")
    public void 에러응답이발생한다() {
        assertThat(world.response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


}
