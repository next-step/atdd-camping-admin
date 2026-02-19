package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class rentalSteps {

    @Autowired private TestContext context;
    @Autowired private ProductRepository productRepository;
    @Autowired private RentalRecordRepository rentalRecordRepository;
    @Autowired private ReservationRepository reservationRepository;

    @Given("대여 가능한 상품이 등록되어 있다")
    public void 대여가능한상품이등록되어있다() {
        Product product = productRepository.save(
                new Product("랜턴", 20, BigDecimal.valueOf(30000), ProductType.RENTAL)
        );
        context.productId = product.getId();
        context.productStockBefore = product.getStockQuantity();
    }

    @Given("대여 기록이 존재한다")
    public void 대여기록이존재한다() {
        Reservation reservation = reservationRepository.findById(context.reservationId).orElseThrow();
        Product product = productRepository.findById(context.productId).orElseThrow();
        RentalRecord record = rentalRecordRepository.save(new RentalRecord(reservation, product, 2));
        context.rentalRecordId = record.getId();
    }

    @When("대여 목록을 조회한다")
    public void 대여목록을조회한다() {
        context.response = context.authRequest().get("/admin/rentals");
    }

    @When("예약 고객에게 장비를 대여한다")
    public void 예약고객에게장비를대여한다() {
        context.response = context.authRequest()
                .body(Map.of("reservationId", context.reservationId, "productId", context.productId, "quantity", 2))
                .post("/admin/rentals");
        if (context.response.statusCode() == 201) {
            context.rentalRecordId = context.response.jsonPath().getLong("id");
        }
    }

    @When("워크인 고객에게 장비를 대여한다")
    public void 워크인고객에게장비를대여한다() {
        context.response = context.authRequest()
                .body(Map.of("productId", context.productId, "quantity", 1))
                .post("/admin/rentals");
        if (context.response.statusCode() == 201) {
            context.rentalRecordId = context.response.jsonPath().getLong("id");
        }
    }

    @When("장비 반납을 처리한다")
    public void 장비반납을처리한다() {
        context.response = context.authRequest()
                .patch("/admin/rentals/" + context.rentalRecordId + "/return");
    }

    @Then("대여가 등록된다")
    public void 대여가등록된다() {
        context.response.then().statusCode(201);
    }

    @Then("반납 처리에 성공한다")
    public void 반납처리에성공한다() {
        context.response.then().statusCode(200);
    }

    @And("대여 목록이 반환된다")
    public void 대여목록이반환된다() {
        context.response.then().body("$", not(empty()));
    }

    @And("대여 정보가 반환된다")
    public void 대여정보가반환된다() {
        context.response.then()
                .body("id", notNullValue())
                .body("quantity", greaterThan(0));
    }

    @And("예약 없이 대여된 정보가 반환된다")
    public void 예약없이대여된정보가반환된다() {
        context.response.then().body("reservationId", nullValue());
    }

    @And("반납 상태로 변경된다")
    public void 반납상태로변경된다() {
        context.response.then().body("isReturned", equalTo(true));
    }
}
