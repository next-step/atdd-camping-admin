package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static com.camping.admin.steps.ReservationListSteps.lastResponse;

public class CommonThenSteps {

    @Then("예약 목록을 응답 받았다.")
    public void 예약목록을응답받았다() {
        Response res = lastResponse.then().extract().response();
        if (res.statusCode() != 200) {
            System.out.println("[ATDD] >>> Unexpected status: " + res.statusCode());
            System.out.println("[ATDD] >>> Body: " + res.asString());
            System.out.println("[ATDD] >>> Headers: " + res.getHeaders());
        }
        lastResponse.then().statusCode(200);
    }

    @And("응답 본문은 유효한 예약 목록이다.")
    public void 응답본문은유효한예약목록이다() {
        // 루트가 JSON 배열인지 확인한다
        lastResponse.then().body("", isA(List.class));

        // 비어있어도 실패는 아님.
        // 비어있지 않다면 1개만 샘플로 약한 스키마 검증 (id/ status 존재)
        JsonPath jp = lastResponse.then().extract().jsonPath();
        List<?> list = jp.getList("$");
        if (list != null && !list.isEmpty()) {
            lastResponse.then()
                    .body("[0].id", notNullValue())
                    .body("[0].status", notNullValue());
        }
    }
}
