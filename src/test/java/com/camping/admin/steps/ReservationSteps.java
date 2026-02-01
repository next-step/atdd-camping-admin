package com.camping.admin.steps;

import com.camping.admin.fixtures.ScenarioContext;
import com.camping.admin.helpers.DatabaseHelper;
import com.camping.admin.helpers.ReservationApiHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

public class ReservationSteps {

    private final ScenarioContext context;
    private final DatabaseHelper db;
    private final ReservationApiHelper api;

    public ReservationSteps(ScenarioContext context, DatabaseHelper db, ReservationApiHelper api) {
        this.context = context;
        this.db = db;
        this.api = api;
    }

    // === Given ===

    @Given("{string} 상태의 예약이 존재한다")
    public void 예약_생성(String status) {
        context.currentReservationId = db.setupReservation(status);
    }

    @And("해당 예약에 반납되지 않은 대여 기록이 있다")
    public void 대여기록_포함_예약_생성() {
        context.currentReservationId = db.setupReservationWithRental("CONFIRMED");
    }

    @Given("예약 ID {long}는 존재하지 않는다")
    public void 존재하지_않는_예약(Long id) {
        // 존재하지 않는 ID - 별도 설정 불필요
    }

    // === When ===

    @When("관리자가 해당 예약을 취소하면")
    public void 예약_취소() {
        context.lastResponse = api.changeStatus(context.currentReservationId, "CANCELLED");
    }

    @When("관리자가 예약 ID {long}를 취소하면")
    public void 특정_예약_취소(Long id) {
        context.lastResponse = api.changeStatus(id, "CANCELLED");
    }

    @When("관리자가 해당 예약을 동시에 {int}번 취소하면")
    public void 동시_예약_취소(int count) throws InterruptedException {
        var executor = Executors.newFixedThreadPool(count);
        var futures = new ArrayList<CompletableFuture<Response>>();

        for (var i = 0; i < count; i++) {
            futures.add(CompletableFuture.supplyAsync(
                () -> api.changeStatus(context.currentReservationId, "CANCELLED"),
                executor
            ));
        }

        context.concurrentResponses = futures.stream()
            .map(CompletableFuture::join)
            .toList();

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    // === Then ===

    @Then("예약 상태가 {string}로 변경된다")
    public void 예약_상태_검증(String expectedStatus) {
        var actualStatus = api.getStatus(context.currentReservationId);
        assertThat(actualStatus)
            .as("예약 상태")
            .isEqualTo(expectedStatus);
    }

    @And("연관된 대여 기록이 반납 처리된다")
    public void 대여_반납_검증() {
        api.getRentals(context.currentReservationId)
            .then()
            .body("rentals[0].isReturned", is(true));
    }

    @Then("{int}번의 취소만 성공한다")
    public void 동시_취소_성공_횟수_검증(int expectedSuccessCount) {
        var actualSuccess = context.concurrentResponses.stream()
            .filter(r -> r.statusCode() == 200)
            .count();

        assertThat(actualSuccess)
            .as("성공한 취소 요청 횟수")
            .isEqualTo(expectedSuccessCount);
    }

    @And("나머지 요청은 {string} 오류가 발생한다")
    public void 동시_취소_실패_검증(String expectedMessage) {
        var failedResponses = context.concurrentResponses.stream()
            .filter(r -> r.statusCode() != 200)
            .toList();

        assertThat(failedResponses)
            .as("실패한 요청이 존재해야 함")
            .isNotEmpty();

        failedResponses.forEach(r ->
            assertThat(r.jsonPath().getString("message"))
                .as("실패 응답의 오류 메시지")
                .isEqualTo(expectedMessage)
        );
    }
}
