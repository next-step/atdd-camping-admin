package com.camping.admin.helper;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.common.CommonHooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.camping.admin.apiExtractableresponse.RentalApiExtractableResponse.대여를_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대여 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class RentalTestHelper {

    private static final Long 존재하지_않는_상품_ID = 999999L;

    @Autowired
    private RentalRecordRepository rentalRecordRepository;

    @Autowired
    private ProductTestHelper productTestHelper;

    @Autowired
    private ReservationTestHelper reservationTestHelper;

    private Product currentProduct;
    private Reservation currentReservation;
    private int initialStock;

    // ==================== 상품 설정 (Given) ====================

    public void 대여용_상품을_재고와_함께_등록한다(int stockQuantity) {
        this.currentProduct = productTestHelper.대여용_상품을_생성한다(stockQuantity);
        this.initialStock = stockQuantity;
    }

    public void 판매용_상품을_등록한다() {
        this.currentProduct = productTestHelper.판매용_상품을_생성한다();
    }

    public void 예약을_조회한다() {
        this.currentReservation = reservationTestHelper.첫번째_예약을_조회한다();
    }

    // ==================== API 호출 (When) ====================

    public void 예약에_연결하여_대여를_요청한다(int quantity) {
        CommonHooks.lastResponse = 대여를_생성한다(currentProduct.getId(), quantity, currentReservation.getId());
    }

    public void 대여를_요청한다(int quantity) {
        CommonHooks.lastResponse = 대여를_생성한다(currentProduct.getId(), quantity, null);
    }

    public void 존재하지_않는_상품_대여를_요청한다() {
        CommonHooks.lastResponse = 대여를_생성한다(존재하지_않는_상품_ID, 1, null);
    }

    public void 예약_없이_대여를_요청한다(int quantity) {
        CommonHooks.lastResponse = 대여를_생성한다(currentProduct.getId(), quantity, null);
    }

    // ==================== 검증 (Then) ====================

    public void 대여_내역을_조회하여_검증한다() {
        Long createdId = CommonHooks.lastResponse.jsonPath().getLong("id");

        RentalRecord rentalRecord = rentalRecordRepository.findById(createdId)
                .orElseThrow(() -> new AssertionError("생성된 대여 기록을 찾을 수 없습니다. ID: " + createdId));

        assertThat(rentalRecord.getProduct().getId()).isEqualTo(currentProduct.getId());
    }

    public void 대여_기록이_생성되지_않았는지_검증한다() {
        int statusCode = CommonHooks.lastResponse.statusCode();
        assertThat(statusCode).isNotEqualTo(201);
    }

    public void 대여를_조회하여_미반납_상태인지_검증한다() {
        RentalRecord latestRental = 가장_최근_대여_기록을_조회한다();
        assertThat(latestRental.getIsReturned()).isFalse();
    }

    public void 상품을_조회하여_재고를_검증한다(int expectedStock) {
        productTestHelper.재고를_검증한다(currentProduct.getId(), expectedStock);
    }

    public void 상품_재고가_유지되는지_검증한다(int expectedStock) {
        상품을_조회하여_재고를_검증한다(expectedStock);
    }

    public void 대여를_조회하여_예약과_연결되지_않았는지_검증한다() {
        RentalRecord latestRental = 가장_최근_대여_기록을_조회한다();
        assertThat(latestRental.getReservation()).isNull();
    }

    // ==================== Private ====================

    private RentalRecord 가장_최근_대여_기록을_조회한다() {
        return rentalRecordRepository.findAll().stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("대여 기록이 존재하지 않습니다."));
    }
}