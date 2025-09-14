package com.camping.admin.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
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
}