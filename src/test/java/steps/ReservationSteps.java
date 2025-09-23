package steps;

import hooks.AuthHooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static api.ReservationApiClient.sendChangeStatus;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    long reservationId;
    ExtractableResponse<Response> response;
    String accessToken = AuthHooks.getAccessToken();

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        // 기본 데이터 사용
        reservationId = 1;
    }

    @When("관리자가 예약을 {string} 상태로 변경한다")
    public void 관리자가예약상태를변경한다(String status) {
        response = sendChangeStatus(accessToken, reservationId, Map.of("status", status));
    }

    @Then("예약은 취소된다")
    public void 예약은취소된다() {
        String status = response.jsonPath().getString("status");
        long reservationId = response.jsonPath().getInt("id");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(reservationId).isEqualTo(this.reservationId);
        assertThat(status).isEqualTo("CANCELLED");
    }

    @Given("예약이 존재하지 않는다")
    public void 예약이존재하지않는다() {
        reservationId = 99999;
    }

    @Then("변경은 실패한다")
    public void 변경은실패한다() {
        assertThat(response.statusCode()).isEqualTo(500);
    }
}
