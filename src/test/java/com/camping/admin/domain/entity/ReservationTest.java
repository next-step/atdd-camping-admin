package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("CONFIRMED에서 CHECKED_IN으로 상태 전환할 수 있다")
    void CONFIRMED에서_CHECKED_IN으로_상태_전환할_수_있다() {
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
    @DisplayName("CHECKED_IN에서 CHECKED_OUT으로 상태 전환할 수 있다")
    void CHECKED_IN에서_CHECKED_OUT으로_상태_전환할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("박민수", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED); // 먼저 CONFIRMED로 설정
        reservation.changeStatus(ReservationStatus.CHECKED_IN); // 그 다음 CHECKED_IN으로 전환

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
    @DisplayName("동일한 상태로 변경 시 예외 발생")
    void 동일한_상태로_변경_시_예외_발생() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("오세훈", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when & then
        assertThatThrownBy(() -> reservation.changeStatus(ReservationStatus.CONFIRMED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("CONFIRMED 상태에서 CONFIRMED 상태로 변경할 수 없습니다");
    }

    @Test
    @DisplayName("유효하지 않은 상태 전환 시 예외 발생")
    void 유효하지_않은_상태_전환_시_예외_발생() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("유지민", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when & then - CONFIRMED에서 CHECKED_OUT으로 바로 전환 불가
        assertThatThrownBy(() -> reservation.changeStatus(ReservationStatus.CHECKED_OUT))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("CONFIRMED 상태에서 CHECKED_OUT 상태로 변경할 수 없습니다");
    }

    @Test
    @DisplayName("null 상태에서는 모든 상태로 전환 가능")
    void null_상태에서는_모든_상태로_전환_가능() {
        // given
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("테스트", LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        // 생성자에서 기본적으로 CONFIRMED로 설정되므로 null로 강제 설정
        reservation.setStatus(null);

        // when
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    // 체크인/체크아웃 도메인 로직 테스트
    @Test
    @DisplayName("CONFIRMED 상태에서 체크인할 수 있다")
    void CONFIRMED_상태에서_체크인할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        LocalDate today = LocalDate.now();
        Reservation reservation = new Reservation("홍길동", today, today.plusDays(2), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when
        reservation.checkIn();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
    }

    @Test
    @DisplayName("CHECKED_IN 상태에서 체크아웃할 수 있다")
    void CHECKED_IN_상태에서_체크아웃할_수_있다() {
        // given
        Campsite campsite = new Campsite();
        LocalDate today = LocalDate.now();
        Reservation reservation = new Reservation("김철수", today, today.plusDays(2), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);
        reservation.checkIn(); // CHECKED_IN 상태로 변경

        // when
        reservation.checkOut();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_OUT);
    }

    @Test
    @DisplayName("체크인 날짜 이전에는 체크인할 수 없다")
    void 체크인_날짜_이전에는_체크인할_수_없다() {
        // given
        Campsite campsite = new Campsite();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Reservation reservation = new Reservation("이영희", tomorrow, tomorrow.plusDays(2), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when & then
        assertThatThrownBy(reservation::checkIn)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("체크인 날짜가 아직 되지 않았습니다");
    }

    @Test
    @DisplayName("이미 체크인된 예약은 다시 체크인할 수 없다")
    void 이미_체크인된_예약은_다시_체크인할_수_없다() {
        // given
        Campsite campsite = new Campsite();
        LocalDate today = LocalDate.now();
        Reservation reservation = new Reservation("박민수", today, today.plusDays(2), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);
        reservation.checkIn(); // 먼저 체크인

        // when & then
        assertThatThrownBy(reservation::checkIn)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 체크인된 예약입니다");
    }

    @Test
    @DisplayName("체크인하지 않은 예약은 체크아웃할 수 없다")
    void 체크인하지_않은_예약은_체크아웃할_수_없다() {
        // given
        Campsite campsite = new Campsite();
        LocalDate today = LocalDate.now();
        Reservation reservation = new Reservation("최수정", today, today.plusDays(2), campsite);
        reservation.changeStatus(ReservationStatus.CONFIRMED);

        // when & then
        assertThatThrownBy(reservation::checkOut)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("체크인하지 않은 예약은 체크아웃할 수 없습니다");
    }
}