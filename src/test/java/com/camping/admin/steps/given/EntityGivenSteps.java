package com.camping.admin.steps.given;

import com.camping.admin.fixture.TestConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.Given;

public class EntityGivenSteps {

    private final SharedState state;

    public EntityGivenSteps(SharedState state) {
        this.state = state;
    }

    // === 상품 ===
    @Given("대여 가능한 상품이 존재한다")
    public void 대여_가능한_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.LANTERN);
    }

    @Given("판매 가능한 상품이 존재한다")
    public void 판매_가능한_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.FIREWOOD);
    }

    @Given("수정할 상품이 존재한다")
    public void 수정할_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.LANTERN);
    }

    @Given("존재하지 않는 상품 ID를 사용한다")
    public void 존재하지_않는_상품_ID를_사용한다() {
        state.setProductId(TestConfig.ProductIds.NOT_EXIST);
    }

    // === 대여 기록 ===
    @Given("반납되지 않은 대여 기록이 존재한다")
    public void 반납되지_않은_대여_기록이_존재한다() {
        state.setRentalRecordId(TestConfig.RentalRecordIds.NOT_RETURNED);
    }

    @Given("이미 반납된 대여 기록이 존재한다")
    public void 이미_반납된_대여_기록이_존재한다() {
        state.setRentalRecordId(TestConfig.RentalRecordIds.ALREADY_RETURNED);
    }

    @Given("존재하지 않는 대여 기록 ID를 사용한다")
    public void 존재하지_않는_대여_기록_ID를_사용한다() {
        state.setRentalRecordId(TestConfig.RentalRecordIds.NOT_EXIST);
    }

    // === 예약 ===
    @Given("확정된 예약이 존재한다")
    public void 확정된_예약이_존재한다() {
        state.setReservationId(TestConfig.ReservationIds.CONFIRMED);
    }

    @Given("존재하지 않는 예약 ID를 사용한다")
    public void 존재하지_않는_예약_ID를_사용한다() {
        state.setReservationId(TestConfig.ReservationIds.NOT_EXIST);
    }

    // === 캠프사이트 ===
    @Given("수정할 캠프사이트가 존재한다")
    public void 수정할_캠프사이트가_존재한다() {
        state.setCampsiteId(TestConfig.CampsiteIds.EXISTING);
    }

    @Given("존재하지 않는 캠프사이트 ID를 사용한다")
    public void 존재하지_않는_캠프사이트_ID를_사용한다() {
        state.setCampsiteId(TestConfig.CampsiteIds.NOT_EXIST);
    }
}
