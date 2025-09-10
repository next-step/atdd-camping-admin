package com.camping.admin.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class ReservationTest {

    private Campsite campsite;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        campsite = new Campsite("A-01", "일반 캠프사이트", 4);
        reservation = new Reservation("김철수", 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 3), 
                campsite);
        reservation.setReservationDate(LocalDate.of(2024, 1, 1));
    }

    @DisplayName("예약을 정상적으로 생성할 수 있다")
    @Test
    void createReservation_Success() {
        // when & then
        assertThat(reservation.getCustomerName()).isEqualTo("김철수");
        assertThat(reservation.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(reservation.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 3));
        assertThat(reservation.getCampsite()).isEqualTo(campsite);
    }

    @DisplayName("2박 3일 예약의 수익을 정상적으로 계산할 수 있다")
    @Test
    void calculateReservationRevenue_TwoNights_Success() {
        // when
        BigDecimal revenue = reservation.calculateReservationRevenue();

        // then
        BigDecimal expected = new BigDecimal("100000"); // 2박 * 50000원
        assertThat(revenue).isEqualTo(expected);
    }

    @DisplayName("당일치기 예약의 수익을 정상적으로 계산할 수 있다")
    @Test
    void calculateReservationRevenue_SameDay_Success() {
        // given
        Reservation sameDayReservation = new Reservation("이영희", 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 1), 
                campsite);

        // when
        BigDecimal revenue = sameDayReservation.calculateReservationRevenue();

        // then
        BigDecimal expected = new BigDecimal("50000"); // 최소 1박
        assertThat(revenue).isEqualTo(expected);
    }

    @DisplayName("1박 2일 예약의 수익을 정상적으로 계산할 수 있다")
    @Test
    void calculateReservationRevenue_OneNight_Success() {
        // given
        Reservation oneNightReservation = new Reservation("박민수", 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 2), 
                campsite);

        // when
        BigDecimal revenue = oneNightReservation.calculateReservationRevenue();

        // then
        BigDecimal expected = new BigDecimal("50000"); // 1박
        assertThat(revenue).isEqualTo(expected);
    }

    @DisplayName("7박 8일 예약의 수익을 정상적으로 계산할 수 있다")
    @Test
    void calculateReservationRevenue_SevenNights_Success() {
        // given
        Reservation longStayReservation = new Reservation("최장기", 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 8), 
                campsite);

        // when
        BigDecimal revenue = longStayReservation.calculateReservationRevenue();

        // then
        BigDecimal expected = new BigDecimal("350000"); // 7박 * 50000원
        assertThat(revenue).isEqualTo(expected);
    }

    @DisplayName("특정 날짜에 예약된 예약인지 확인할 수 있다")
    @Test
    void isOnDate_ReservationDate_ReturnsTrue() {
        // when & then
        assertThat(reservation.isOnDate(LocalDate.of(2024, 1, 1))).isTrue();
    }

    @DisplayName("특정 날짜에 예약되지 않은 예약인지 확인할 수 있다")
    @Test
    void isOnDate_DifferentDate_ReturnsFalse() {
        // when & then
        assertThat(reservation.isOnDate(LocalDate.of(2024, 1, 2))).isFalse();
    }

    @DisplayName("예약 날짜가 없는 경우 false를 반환한다")
    @Test
    void isOnDate_NoReservationDate_ReturnsFalse() {
        // given
        reservation.setReservationDate(null);

        // when & then
        assertThat(reservation.isOnDate(LocalDate.of(2024, 1, 1))).isFalse();
    }

    @DisplayName("날짜 범위 안에 있는 예약인지 확인할 수 있다")
    @Test
    void isInDateRange_WithinRange_ReturnsTrue() {
        // when & then
        assertThat(reservation.isInDateRange(
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 31)
        )).isTrue();
    }

    @DisplayName("날짜 범위 시작일에 있는 예약인지 확인할 수 있다")
    @Test
    void isInDateRange_OnStartDate_ReturnsTrue() {
        // when & then
        assertThat(reservation.isInDateRange(
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 31)
        )).isTrue();
    }

    @DisplayName("날짜 범위 종료일에 있는 예약인지 확인할 수 있다")
    @Test
    void isInDateRange_OnEndDate_ReturnsTrue() {
        // when & then
        assertThat(reservation.isInDateRange(
                LocalDate.of(2023, 12, 1), 
                LocalDate.of(2024, 1, 1)
        )).isTrue();
    }

    @DisplayName("날짜 범위 밖에 있는 예약인지 확인할 수 있다")
    @Test
    void isInDateRange_OutsideRange_ReturnsFalse() {
        // when & then
        assertThat(reservation.isInDateRange(
                LocalDate.of(2024, 2, 1), 
                LocalDate.of(2024, 2, 28)
        )).isFalse();
    }

    @DisplayName("예약 날짜가 없는 경우 날짜 범위 확인에서 false를 반환한다")
    @Test
    void isInDateRange_NoReservationDate_ReturnsFalse() {
        // given
        reservation.setReservationDate(null);

        // when & then
        assertThat(reservation.isInDateRange(
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 31)
        )).isFalse();
    }
}