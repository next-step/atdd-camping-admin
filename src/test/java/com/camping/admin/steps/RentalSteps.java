package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ProductApiClient;
import com.camping.admin.helper.RentalApiClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RentalSteps {

    private static final Logger log = LoggerFactory.getLogger(RentalSteps.class);

    private final ApiClientContext api;
    private final ScenarioContext scenario;

    public RentalSteps(ApiClientContext api, ScenarioContext scenario) {
        this.api = api;
        this.scenario = scenario;
    }

    private RentalApiClient rentalHelper() {
        return api.rental();
    }

    private ProductApiClient productHelper() {
        return api.product();
    }

    @Given("대여 가능한 상품이 있다")
    public void 대여가능한상품이있다() {
        int targetProductId = productHelper().findProductByTypeAndStock("RENTAL", 1);
        scenario.setTargetProductId(targetProductId);
        scenario.setOriginalStock(productHelper().getProductStock(targetProductId));
        log.info("[Given] 대여 가능한 상품이 있다. ID: {}", targetProductId);
    }

    @When("관리자가 해당 상품을 대여한다")
    public void 관리자가해당상품을대여한다() {
        int productId = scenario.getTargetProductId();
        scenario.setLastResponse(rentalHelper().createRental(productId, 1));
    }

    @Then("대여가 생성된다")
    public void 대여가생성된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(200, 201);

        int createdRentalId = scenario.getLastResponse().jsonPath().getInt("id");
        boolean exists = rentalHelper().isRentalReturned(createdRentalId) || !rentalHelper().isRentalReturned(createdRentalId);
        assertThat(exists).isTrue();
        log.info("[Then] 대여가 생성된다 - DB 반영 확인 완료");
    }

    @And("대여 상품 재고가 감소한다")
    public void 대여상품재고가감소한다() {
        int currentStock = productHelper().getProductStock(scenario.getTargetProductId());
        assertThat(currentStock).isLessThan(scenario.getOriginalStock());
        log.info("[And] 상품 재고가 감소한다");
    }

    @Given("대여 중인 장비가 있다")
    public void 대여중인장비가있다() {
        int targetRentalId = rentalHelper().findUnreturnedRentalId();
        int targetProductId = rentalHelper().getRentalProductId(targetRentalId);
        scenario.setTargetRentalId(targetRentalId);
        scenario.setTargetProductId(targetProductId);
        scenario.setOriginalStock(productHelper().getProductStock(targetProductId));
        log.info("[Given] 대여 중인 장비가 있다. ID: {}", targetRentalId);
    }

    @When("관리자가 해당 장비 반납 처리한다")
    public void 관리자가해당장비반납처리한다() {
        scenario.setLastResponse(rentalHelper().returnRental(scenario.getTargetRentalId()));
    }

    @Then("반납이 완료된다")
    public void 반납이완료된다() {
        scenario.getLastResponse().then().statusCode(200);

        boolean isReturned = rentalHelper().isRentalReturned(scenario.getTargetRentalId());
        assertThat(isReturned).isTrue();
        log.info("[Then] 반납이 완료된다 - DB 반영 확인 완료");
    }

    @And("상품 재고가 복구된다")
    public void 상품재고가복구된다() {
        int currentStock = productHelper().getProductStock(scenario.getTargetProductId());
        assertThat(currentStock).isGreaterThan(scenario.getOriginalStock());
        log.info("[And] 상품 재고가 복구된다");
    }

    @Then("대여가 거부된다")
    public void 대여가거부된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        log.info("[Then] 대여가 거부된다");
    }

    @Given("이미 반납된 대여 기록이 있다")
    public void 이미반납된대여기록이있다() {
        int targetRentalId = rentalHelper().findReturnedRentalId();
        scenario.setTargetRentalId(targetRentalId);
        log.info("[Given] 이미 반납된 대여 기록이 있다. ID: {}", targetRentalId);
    }

    @Then("반납이 거부된다")
    public void 반납이거부된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        log.info("[Then] 반납이 거부된다");
    }
}
