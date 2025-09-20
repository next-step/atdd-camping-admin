package steps;

import hooks.AuthHooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static api.ReservationApiClient.sendChangeStatus;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    long reservationId;
    ExtractableResponse<Response> response;

    @Given("사용자가 예약을 했다.")
    public void 사용자가예약을했다() {
        // 기본 데이터 사용
        reservationId = 1;
    }

    @When("관리자가 예약을 취소한다.")
    public void 관리자가예약을취소한다() {
        String accessToken = AuthHooks.getAccessToken();
        response = sendChangeStatus(accessToken, reservationId, Map.of("status", "CANCELLED"));
    }

    @Then("예약은 취소된다.")
    public void 예약은취소된다() {
        String status = response.jsonPath().getString("status");
        long reservationId = response.jsonPath().getInt("id");

        assertThat(reservationId).isEqualTo(this.reservationId);
        assertThat(status).isEqualTo("CANCELLED");
    }
}
