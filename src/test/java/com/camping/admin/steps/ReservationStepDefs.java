package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.support.api.ReservationApi;
import com.camping.admin.support.context.ReservationWorld;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;

public class ReservationStepDefs {

    private final ReservationWorld world;
    private final ReservationApi reservationApi;

    public ReservationStepDefs(ReservationWorld world, ReservationApi reservationApi) {
        this.world = world;
        this.reservationApi = reservationApi;
    }

    @Given("관리자가 로그인을 하였다.")
    public void 관리자가로그인을하였다() {
        assertThat(world.common.authToken).isNotBlank();
    }

    @Given("예약 ID 1 이 존재한다.")
    public void 예약ID이존재한다() {
        world.reservationId = 1L;
    }

    @When("관리자가 해당 예약을 조회한다.")
    public void 관리자가예약ID을조회한다() {
        world.common.response = reservationApi.find(world.common.authToken, world.reservationId);
    }

    @Then("예약 ID 1 의 상세 정보가 조회된다.")
    public void 예약ID의상세정보가조회된다() {
        assertThat(world.common.response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(world.common.response.jsonPath().getLong("id")).isEqualTo(1L);
    }

    @Given("존재하지 않는 예약 ID 9999 가 있다.")
    public void 존재하지않는예약ID가있다() {
        world.reservationId = 9999L;
    }

    @Given("WAITING 상태인 예약이 존재한다.")
    public void wating상태인예약이존재한다() {
        world.reservationId = 13L;
    }

    @When("관리자가 WAITING 상태인 예약 상태를 PENDING 상태로 변경한다.")
    public void 관리자가WAITING상태인예약상태를PENDING상태로변경한다() {
        var response = reservationApi.patchStatus(world.common.authToken, world.reservationId,
                "PENDING");
        world.status = response.jsonPath().get("status");
    }

    @Then("예약 상태가 PENDING 으로 변경된다.")
    public void 예약상태가PENDING으로변경된다() {
        assertThat(world.status).isEqualTo("PENDING");
    }

    @When("관리자가 예약 ID 9999 의 상태를 PENDING 상태로 변경한다.")
    public void 관리자가예약ID의상태를PENDING상태로변경한다() {
        world.common.response = reservationApi.patchStatus(world.common.authToken,
                world.reservationId, "PENDING");
    }

    @Then("에러 응답이 발생한다.")
    public void 에러응답이발생한다() {
        assertThat(world.common.response.statusCode()).isEqualTo(
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @When("특정 예약에 대해 잘못된 상태로 예약 상태를 변경한다.")
    public void 특정예약에대해잘못된상태로예약상태를변경한다() {
        world.common.response = reservationApi.patchStatus(world.common.authToken, 1, "");
    }

    @Then("잘못된 요청 응답이 발생한다.")
    public void 잘못된요청응답이발생한다() {
        assertThat(world.common.response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
