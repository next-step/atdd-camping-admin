package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    @Autowired private TestContext context;
    @Autowired private CampsiteRepository campsiteRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ProductRepository productRepository;

    @Given("캠프사이트가 등록되어 있다")
    public void 캠프사이트가등록되어있다() {
        Campsite campsite = campsiteRepository.save(new Campsite("T-01", "테스트 사이트", 4));
        context.campsiteId = campsite.getId();
    }

    @And("확정된 예약이 존재한다")
    public void 확정된예약이존재한다() {
        Campsite campsite = campsiteRepository.findById(context.campsiteId).orElseThrow();
        Reservation reservation = reservationRepository.save(
                new Reservation("테스트 고객", LocalDate.now(), LocalDate.now().plusDays(1), campsite)
        );
        context.reservationId = reservation.getId();
    }

    @Then("조회에 성공한다")
    public void 조회에성공한다() {
        context.response.then().statusCode(200);
    }

    @Then("수정에 성공한다")
    public void 수정에성공한다() {
        context.response.then().statusCode(200);
    }

    // HTTP가 아닌 DB 직접 조회이므로 AssertJ 사용
    @And("상품 재고가 감소한다")
    public void 상품재고가감소한다() {
        int updatedStock = productRepository.findById(context.productId).orElseThrow().getStockQuantity();
        assertThat(updatedStock).isLessThan(context.productStockBefore);
    }

    @And("상품 재고가 복구된다")
    public void 상품재고가복구된다() {
        int updatedStock = productRepository.findById(context.productId).orElseThrow().getStockQuantity();
        assertThat(updatedStock).isEqualTo(context.productStockBefore);
    }
}
