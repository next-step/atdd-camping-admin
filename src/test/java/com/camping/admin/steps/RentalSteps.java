package com.camping.admin.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;
import java.util.Optional;

import static com.camping.admin.steps.RentalTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RentalSteps {
    private Long rentalId;
    private int statusCode;
    private ExtractableResponse<Response> rentalListResponse;

    @When("관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.")
    public void 관리자는특정상품의수량만큼대여기록을작성한다(DataTable dataTable) {
        ExtractableResponse<Response> response = 대여_기록_작성_요청(extractRentalBodyFromDataTable(dataTable));
        if (response.statusCode() == 201) {
            rentalId = response.jsonPath().getLong("id");
        }
        statusCode = response.statusCode();
    }

    private Map<String, Integer> extractRentalBodyFromDataTable(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        return Map.of(
                "reservationId", Integer.parseInt(data.get("reservationId")),
                "productId", Integer.parseInt(data.get("productId")),
                "quantity", Integer.parseInt(data.get("quantity"))
        );
    }

    @Then("대여 기록이 생성된다.")
    public void 대여기록이생성된다() {
        assertThat(statusCode).isEqualTo(201);
        Map<String, Object> rental = 특정_대여_기록_조회(rentalId);
        assertThat(rental.get("isReturned")).isEqualTo(false);
    }

    @And("상품의 수량이 대여 기록의 수량 만큼 줄어든다.")
    public void 상품의수량이대여기록의수량만큼줄어든다() {
        Map<String, Object> rental = 특정_대여_기록_조회(rentalId);
        assertThat(rental.get("quantity")).isEqualTo(2);
    }

    @Then("대여에 실패한다.")
    public void 대여에실패한다() {
        assertThat(statusCode).isEqualTo(500);
    }

    @And("대여 기록이 생성되지 않는다.")
    public void 대여기록이생성되지않는다(DataTable dataTable) {
        long productId = Long.parseLong(dataTable.asMap().get("productId"));

        ExtractableResponse<Response> response = 대여_기록_목록_조회();
        Optional<Map> rentalRecord = response.jsonPath().getList(".", Map.class).stream()
                .filter(record -> ((Number) record.get("productId")).longValue() == productId)
                .findFirst();

        assertThat(rentalRecord).isEmpty();
    }

    @Given("대여 기록이 존재한다.")
    public void 대여기록이존재한다() {
        ExtractableResponse<Response> existingRentals = 대여_기록_목록_조회();

        if (existingRentals.jsonPath().getList(".").isEmpty()) {
            Map<String, Integer> rentalData = Map.of(
                    "reservationId", 1,
                    "productId", 1,
                    "quantity", 1
            );
            ExtractableResponse<Response> response = 대여_기록_작성_요청(rentalData);
            assertThat(response.statusCode()).isEqualTo(201);
            rentalId = response.jsonPath().getLong("id");
        }
    }

    @When("관리자가 대여 기록 목록을 조회한다.")
    public void 관리자가대여기록목록을조회한다() {
        rentalListResponse = 대여_기록_목록_조회();
        statusCode = rentalListResponse.statusCode();
    }

    @Then("대여 기록 목록이 조회된다.")
    public void 대여기록목록이조회된다() {
        assertThat(statusCode).isEqualTo(200);
        assertThat(rentalListResponse.jsonPath().getList(".")).isNotEmpty();
    }

    @Given("반납되지 않은 대여 기록이 존재한다.")
    public void 반납되지않은대여기록이존재한다() {
        Map<String, Integer> rentalData = Map.of(
                "reservationId", 1,
                "productId", 1,
                "quantity", 1
        );
        ExtractableResponse<Response> response = 대여_기록_작성_요청(rentalData);
        assertThat(response.statusCode()).isEqualTo(201);
        rentalId = response.jsonPath().getLong("id");

        Map<String, Object> rental = 특정_대여_기록_조회(rentalId);
        assertThat(rental.get("isReturned")).isEqualTo(false);
    }

    @When("관리자가 대여 반납 처리를 한다.")
    public void 관리자가대여반납처리를한다() {
        ExtractableResponse<Response> response = 대여_반납_처리(rentalId);
        statusCode = response.statusCode();
    }

    @Then("대여가 반납 처리된다.")
    public void 대여가반납처리된다() {
        assertThat(statusCode).isEqualTo(200);
        Map<String, Object> rental = 특정_대여_기록_조회(rentalId);
        assertThat(rental.get("isReturned")).isEqualTo(true);
    }

    @When("관리자는 워크인 고객의 대여 기록을 작성한다.")
    public void 관리자는워크인고객의대여기록을작성한다(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Map<String, Integer> walkinRentalData = Map.of(
                "productId", Integer.parseInt(data.get("productId")),
                "quantity", Integer.parseInt(data.get("quantity"))
                // reservationId는 의도적으로 제외 (null)
        );
        ExtractableResponse<Response> response = 대여_기록_작성_요청(walkinRentalData);
        if (response.statusCode() == 201) {
            rentalId = response.jsonPath().getLong("id");
        }
        statusCode = response.statusCode();
    }

    @And("예약 정보가 없는 대여 기록이 생성된다.")
    public void 예약정보가없는대여기록이생성된다() {
        Map<String, Object> rental = 특정_대여_기록_조회(rentalId);
        assertThat(rental.get("reservationId")).isNull();
    }

    @Given("이미 반납된 대여 기록이 존재한다.")
    public void 이미반납된대여기록이존재한다() {
        // 대여 기록 생성
        Map<String, Integer> rentalData = Map.of(
                "reservationId", 1,
                "productId", 1,
                "quantity", 1
        );
        ExtractableResponse<Response> response = 대여_기록_작성_요청(rentalData);
        assertThat(response.statusCode()).isEqualTo(201);
        rentalId = response.jsonPath().getLong("id");

        // 반납 처리
        ExtractableResponse<Response> returnResponse = 대여_반납_처리(rentalId);
        assertThat(returnResponse.statusCode()).isEqualTo(200);

        // 반납 확인
        Map<String, Object> rental = 특정_대여_기록_조회(rentalId);
        assertThat(rental.get("isReturned")).isEqualTo(true);
    }

    @When("관리자가 이미 반납된 대여 기록을 반납 처리한다.")
    public void 관리자가이미반납된대여기록을반납처리한다() {
        ExtractableResponse<Response> response = 대여_반납_처리(rentalId);
        statusCode = response.statusCode();
    }

    @Then("대여 반납이 실패한다.")
    public void 대여반납이실패한다() {
        assertThat(statusCode).isEqualTo(500);
    }

    @Given("존재하지 않는 대여 기록이 있다.")
    public void 존재하지않는대여기록이있다() {
        rentalId = 999L; // 존재하지 않는 ID
    }

    @When("관리자가 존재하지 않는 대여 기록을 반납 처리한다.")
    public void 관리자가존재하지않는대여기록을반납처리한다() {
        ExtractableResponse<Response> response = 대여_반납_처리(rentalId);
        statusCode = response.statusCode();
    }
}