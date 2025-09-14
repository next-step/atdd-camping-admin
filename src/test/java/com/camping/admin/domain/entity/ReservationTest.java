package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    @Test
    @DisplayName("예약 상태를 CONFIRMED로 변경할 수 있다")
    void 예약_상태를_CONFIRMED로_변경할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("홍길동", LocalDate.now(), LocalDate.now().plusDays(1), campsite);

        // when
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("예약 상태를 CANCELLED로 변경할 수 있다")
    void 예약_상태를_CANCELLED로_변경할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("김철수", LocalDate.now(), LocalDate.now().plusDays(2), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when
        reservation.changeStatus(ReservationStatus.CANCELLED);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    @DisplayName("예약 상태를 CHECKED_IN으로 변경할 수 있다")
    void 예약_상태를_CHECKED_IN으로_변경할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("이영희", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when
        reservation.changeStatus(ReservationStatus.CHECKED_IN);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
    }

    @Test
    @DisplayName("예약 상태를 CHECKED_OUT으로 변경할 수 있다")
    void 예약_상태를_CHECKED_OUT으로_변경할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("박민수", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(ReservationStatus.CHECKED_IN);

        // when
        reservation.changeStatus(ReservationStatus.CHECKED_OUT);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_OUT);
    }

    @Test
    @DisplayName("예약 상태를 WAITING에서 PENDING으로 변경할 수 있다")
    void 예약_상태를_WAITING에서_PENDING으로_변경할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("최수정", LocalDate.now(), LocalDate.now().plusDays(3), campsite);
        reservation.changeStatus(ReservationStatus.WAITING);

        // when
        reservation.changeStatus(ReservationStatus.PENDING);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("예약 상태를 PENDING에서 REJECTED로 변경할 수 있다")
    void 예약_상태를_PENDING에서_REJECTED로_변경할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("정하늘", LocalDate.now(), LocalDate.now().plusDays(2), campsite);
        reservation.changeStatus(ReservationStatus.PENDING);

        // when
        reservation.changeStatus(ReservationStatus.REJECTED);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REJECTED);
    }

    @Test
    @DisplayName("동일한 상태로 변경해도 문제없이 처리된다")
    void 동일한_상태로_변경해도_문제없이_처리된다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("오세훈", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("null 상태로 변경할 수 있다")
    void null_상태로_변경할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("유지민", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when
        reservation.changeStatus(null);

        // then
        assertThat(reservation.getStatus()).isNull();
    }
}