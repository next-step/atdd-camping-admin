package features;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationStepDefs {

    private Long reservationId;
    private String adminToken;

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        reservationId = 1L;
    }

    @When("관리자가 예약을 취소했다")
    public void 관리자가예약을취소했다() {
        adminToken = given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");

        Map<String, Object> body = Map.of("status", "CANCELLED");
        ExtractableResponse<Response> response = given().log().all()
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .when()
                .patch("/admin/reservations/{reservationId}/status", reservationId)
                .then().log().all()
                .statusCode(200)
                .extract();
    }

    @Then("예약은 취소 상태다")
    public void 예약은취소상태다() {
        ExtractableResponse<Response> reservationsResponse = given()
                .cookie("AUTH_TOKEN", adminToken)
                .when()
                .get("/admin/reservations")
                .then().statusCode(200)
                .extract();

        String status = reservationsResponse.jsonPath().getString(String.format("find {it.id == %d}.status", reservationId));
        assertNotNull(status, "취소 예약 대상을 조회하지 못했습니다.");
        assertThat(status).isEqualTo("CANCELLED");
    }
}
