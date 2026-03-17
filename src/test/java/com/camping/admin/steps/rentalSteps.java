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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class rentalSteps {

    @Autowired
    private TestContext context;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RentalRecordRepository rentalRecordRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    // ── 공통 헬퍼 ─────────────────────────────────────────────

    private void postRental(Long productId, int quantity, Long reservationId) {
        Map<String, Object> body = new HashMap<>();
        body.put("productId", productId);
        body.put("quantity", quantity);
        if (reservationId != null) {
            body.put("reservationId", reservationId);
        }
        context.response = context.authRequest().body(body).post("/admin/rentals");
        if (context.response.statusCode() == 201) {
            context.rentalRecordId = context.response.jsonPath().getLong("id");
        }
    }

    // ── Given ─────────────────────────────────────────────────

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
        product.setStockQuantity(product.getStockQuantity() - record.getQuantity());
        productRepository.save(product);
    }

    @Given("상품 재고가 모두 소진되어 있다")
    public void 상품재고가모두소진되어있다() {
        Product product = productRepository.findById(context.productId).orElseThrow();
        product.setStockQuantity(0);
        productRepository.save(product);
    }

    @Given("판매 유형의 상품이 등록되어 있다")
    public void 판매유형의상품이등록되어있다() {
        Product product = productRepository.save(
                new Product("판매용 소모품", 10, BigDecimal.valueOf(5000), ProductType.SALE)
        );
        context.productId = product.getId();
    }

    @Given("장비가 이미 반납 처리되어 있다")
    public void 장비가이미반납처리되어있다() {
        RentalRecord record = rentalRecordRepository.findById(context.rentalRecordId).orElseThrow();
        record.setReturned(true);
        rentalRecordRepository.save(record);
    }

    // ── When ──────────────────────────────────────────────────

    @When("대여 목록을 조회한다")
    public void 대여목록을조회한다() {
        context.response = context.authRequest().get("/admin/rentals");
    }

    @When("예약 고객에게 장비를 대여한다")
    public void 예약고객에게장비를대여한다() {
        postRental(context.productId, 2, context.reservationId);
    }

    @When("워크인 고객에게 장비를 대여한다")
    public void 워크인고객에게장비를대여한다() {
        postRental(context.productId, 1, null);
    }

    @When("장비 반납을 처리한다")
    public void 장비반납을처리한다() {
        context.response = context.authRequest()
                .patch("/admin/rentals/" + context.rentalRecordId + "/return");
    }

    @When("존재하지 않는 상품으로 대여를 시도한다")
    public void 존재하지않는상품으로대여를시도한다() {
        postRental(99999L, 1, context.reservationId);
    }

    @When("존재하지 않는 예약으로 대여를 시도한다")
    public void 존재하지않는예약으로대여를시도한다() {
        postRental(context.productId, 1, 99999L);
    }

    @When("수량이 0인 장비 대여를 시도한다")
    public void 수량이0인장비대여를시도한다() {
        postRental(context.productId, 0, context.reservationId);
    }

    @When("음수 수량으로 장비 대여를 시도한다")
    public void 음수수량으로장비대여를시도한다() {
        postRental(context.productId, -1, context.reservationId);
    }

    @When("존재하지 않는 대여 기록을 반납 처리한다")
    public void 존재하지않는대여기록을반납처리한다() {
        context.response = context.authRequest()
                .patch("/admin/rentals/99999/return");
    }

    // ── Then / And ────────────────────────────────────────────

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

    @Then("대여가 거부된다")
    public void 대여가거부된다() {
        context.response.then().statusCode(400);
    }

    @Then("반납 처리가 거부된다")
    public void 반납처리가거부된다() {
        context.response.then().statusCode(400);
    }

    @Then("대여 요청 대상을 찾을 수 없다")
    public void 대여요청대상을찾을수없다() {
        context.response.then().statusCode(404);
    }

    @Then("대여 기록을 찾을 수 없다")
    public void 대여기록을찾을수없다() {
        context.response.then().statusCode(404);
    }
}
