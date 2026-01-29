package com.camping.admin.helper;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.common.CommonHooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.camping.admin.apiExtractableresponse.RentalApiExtractableResponse.대여를_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대여 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class RentalTestHelper {

    private static final Long 존재하지_않는_상품_ID = 999999L;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RentalRecordRepository rentalRecordRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Product currentProduct;
    private Reservation currentReservation;
    private long rentalCountBeforeRequest;
    private int initialStock;

    // ==================== 상품 설정 (Given) ====================

    public void 대여용_상품을_재고와_함께_등록한다(int stockQuantity) {
        this.currentProduct = productRepository.save(
                new Product("테스트 대여 상품", stockQuantity, new BigDecimal("10000"), ProductType.RENTAL)
        );
        this.initialStock = stockQuantity;
    }

    public void 판매용_상품을_등록한다() {
        this.currentProduct = productRepository.save(
                new Product("테스트 판매 상품", 10, new BigDecimal("5000"), ProductType.SALE)
        );
    }

    public void 예약을_조회한다() {
        this.currentReservation = reservationRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("예약이 존재하지 않습니다."));
    }

    // ==================== API 호출 (When) ====================

    public void 예약에_연결하여_대여를_요청한다(int quantity) {
        this.rentalCountBeforeRequest = rentalRecordRepository.count();
        CommonHooks.lastResponse = 대여를_생성한다(currentProduct.getId(), quantity, currentReservation.getId());
    }

    public void 대여를_요청한다(int quantity) {
        this.rentalCountBeforeRequest = rentalRecordRepository.count();
        CommonHooks.lastResponse = 대여를_생성한다(currentProduct.getId(), quantity, null);
    }

    public void 존재하지_않는_상품_대여를_요청한다() {
        this.rentalCountBeforeRequest = rentalRecordRepository.count();
        CommonHooks.lastResponse = 대여를_생성한다(존재하지_않는_상품_ID, 1, null);
    }

    public void 예약_없이_대여를_요청한다(int quantity) {
        this.rentalCountBeforeRequest = rentalRecordRepository.count();
        CommonHooks.lastResponse = 대여를_생성한다(currentProduct.getId(), quantity, null);
    }

    // ==================== 검증 (Then) ====================

    public void 대여_기록이_생성되었는지_검증한다() {
        long currentCount = rentalRecordRepository.count();
        assertThat(currentCount).isEqualTo(rentalCountBeforeRequest + 1);
    }

    public void 대여_기록이_생성되지_않았는지_검증한다() {
        long currentCount = rentalRecordRepository.count();
        assertThat(currentCount).isEqualTo(rentalCountBeforeRequest);
    }

    public void 대여가_미반납_상태인지_검증한다() {
        RentalRecord latestRental = 가장_최근_대여_기록을_조회한다();
        assertThat(latestRental.getIsReturned()).isFalse();
    }

    public void 상품_재고를_검증한다(int expectedStock) {
        Product product = productRepository.findById(currentProduct.getId()).orElseThrow();
        assertThat(product.getStockQuantity()).isEqualTo(expectedStock);
    }

    public void 상품_재고가_유지되는지_검증한다(int expectedStock) {
        상품_재고를_검증한다(expectedStock);
    }

    public void 대여가_예약과_연결되지_않았는지_검증한다() {
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