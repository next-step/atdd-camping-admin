package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.RentalConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RentalRecordTest {

    private Product product;
    private Reservation reservation;
    private RentalRecord rentalRecord;

    @BeforeEach
    void setUp() {
        product = new Product("텐트", 10, new BigDecimal("50000"), ProductType.RENTAL);
        reservation = new Reservation(
                "김철수",
                java.time.LocalDate.of(2024, 1, 1),
                java.time.LocalDate.of(2024, 1, 3),
                new Campsite("A-01", "일반 캠프사이트", 4)
        );
        rentalRecord = new RentalRecord(reservation, product, 2);
    }

    @DisplayName("대여 기록을 정상적으로 생성할 수 있다")
    @Test
    void createRentalRecord_Success() {
        assertThat(rentalRecord.getReservation()).isEqualTo(reservation);
        assertThat(rentalRecord.getProduct()).isEqualTo(product);
        assertThat(rentalRecord.getQuantity()).isEqualTo(2);
        assertThat(rentalRecord.getIsReturned()).isFalse();
        assertThat(rentalRecord.getCreatedAt()).isNotNull();
    }

    @DisplayName("예약 없이 대여 기록을 생성할 수 있다")
    @Test
    void createRentalRecord_WithoutReservation_Success() {
        RentalRecord walkInRental = new RentalRecord(null, product, 1);

        assertThat(walkInRental.getReservation()).isNull();
        assertThat(walkInRental.getProduct()).isEqualTo(product);
        assertThat(walkInRental.getQuantity()).isEqualTo(1);
        assertThat(walkInRental.getIsReturned()).isFalse();
    }

    @DisplayName("반납 처리를 정상적으로 할 수 있다")
    @Test
    void markAsReturned_Success() {
        rentalRecord.markAsReturned();

        assertThat(rentalRecord.getIsReturned()).isTrue();
    }

    @DisplayName("이미 반납된 상품을 다시 반납할 때 예외가 발생한다")
    @Test
    void markAsReturned_AlreadyReturned_ThrowsException() {
        rentalRecord.markAsReturned();

        assertThatThrownBy(() -> rentalRecord.markAsReturned())
                .isInstanceOf(RentalConflictException.class)
                .hasMessageContaining("already been returned");
    }

    @DisplayName("대여 수익을 정상적으로 계산할 수 있다")
    @Test
    void calculateRentalRevenue_Success() {
        BigDecimal revenue = rentalRecord.calculateRentalRevenue();

        BigDecimal expected = new BigDecimal("50000").multiply(new BigDecimal("2"));
        assertThat(revenue).isEqualTo(expected);
    }

    @DisplayName("수량이 1인 경우 대여 수익을 정상적으로 계산할 수 있다")
    @Test
    void calculateRentalRevenue_QuantityOne_Success() {
        RentalRecord singleRental = new RentalRecord(reservation, product, 1);

        BigDecimal revenue = singleRental.calculateRentalRevenue();

        assertThat(revenue).isEqualTo(new BigDecimal("50000"));
    }

    @DisplayName("가격이 다른 상품의 대여 수익을 정상적으로 계산할 수 있다")
    @Test
    void calculateRentalRevenue_DifferentPrice_Success() {
        Product expensiveProduct = new Product("고급 텐트", 5, new BigDecimal("100000"), ProductType.RENTAL);
        RentalRecord expensiveRental = new RentalRecord(reservation, expensiveProduct, 1);

        BigDecimal revenue = expensiveRental.calculateRentalRevenue();

        assertThat(revenue).isEqualTo(new BigDecimal("100000"));
    }
}
