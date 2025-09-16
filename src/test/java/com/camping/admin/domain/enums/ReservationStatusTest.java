package com.camping.admin.domain.enums;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ReservationStatus 상태 전환 로직 테스트")
class ReservationStatusTest {

    private Campsite campsite;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        campsite = new Campsite();
        LocalDate today = LocalDate.now();
        reservation = new Reservation("홍길동", today, today.plusDays(2), campsite);
    }

    @Nested
    @DisplayName("상태 전환 가능성 테스트")
    class TransitionValidationTest {

        @Test
        @DisplayName("WAITING 상태에서 가능한 전환")
        void WAITING_상태_전환() {
            // given
            ReservationStatus waiting = ReservationStatus.WAITING;

            // then
            assertThat(waiting.canTransitionTo(ReservationStatus.PENDING)).isTrue();
            assertThat(waiting.canTransitionTo(ReservationStatus.CONFIRMED)).isTrue();
            assertThat(waiting.canTransitionTo(ReservationStatus.REJECTED)).isTrue();
            assertThat(waiting.canTransitionTo(ReservationStatus.CANCELLED)).isTrue();
            assertThat(waiting.canTransitionTo(ReservationStatus.CHECKED_IN)).isFalse();
            assertThat(waiting.canTransitionTo(ReservationStatus.CHECKED_OUT)).isFalse();
        }

        @Test
        @DisplayName("CONFIRMED 상태에서 가능한 전환")
        void CONFIRMED_상태_전환() {
            // given
            ReservationStatus confirmed = ReservationStatus.CONFIRMED;

            // then
            assertThat(confirmed.canTransitionTo(ReservationStatus.CHECKED_IN)).isTrue();
            assertThat(confirmed.canTransitionTo(ReservationStatus.CANCELLED)).isTrue();
            assertThat(confirmed.canTransitionTo(ReservationStatus.PENDING)).isFalse();
            assertThat(confirmed.canTransitionTo(ReservationStatus.REJECTED)).isFalse();
            assertThat(confirmed.canTransitionTo(ReservationStatus.CHECKED_OUT)).isFalse();
        }

        @Test
        @DisplayName("CHECKED_IN 상태에서 가능한 전환")
        void CHECKED_IN_상태_전환() {
            // given
            ReservationStatus checkedIn = ReservationStatus.CHECKED_IN;

            // then
            assertThat(checkedIn.canTransitionTo(ReservationStatus.CHECKED_OUT)).isTrue();
            assertThat(checkedIn.canTransitionTo(ReservationStatus.CONFIRMED)).isFalse();
            assertThat(checkedIn.canTransitionTo(ReservationStatus.CANCELLED)).isFalse();
        }

        @Test
        @DisplayName("종료 상태들은 다른 상태로 전환 불가")
        void 종료_상태_전환_불가() {
            // given
            ReservationStatus[] finalStates = {
                ReservationStatus.CHECKED_OUT,
                ReservationStatus.REJECTED,
                ReservationStatus.CANCELLED
            };

            // then
            for (ReservationStatus finalState : finalStates) {
                for (ReservationStatus targetState : ReservationStatus.values()) {
                    assertThat(finalState.canTransitionTo(targetState))
                        .as("%s 상태는 %s로 전환할 수 없어야 함", finalState, targetState)
                        .isFalse();
                }
            }
        }

        @Test
        @DisplayName("유효하지 않은 상태 전환 시 예외 발생")
        void 유효하지_않은_상태_전환_예외() {
            // given
            ReservationStatus confirmed = ReservationStatus.CONFIRMED;

            // when & then
            assertThatThrownBy(() -> confirmed.validateTransition(ReservationStatus.CHECKED_OUT))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("CONFIRMED 상태에서 CHECKED_OUT 상태로 변경할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("체크인 로직 테스트")
    class CheckInLogicTest {

        @Test
        @DisplayName("CONFIRMED 상태에서 체크인 성공")
        void CONFIRMED_체크인_성공() {
            // given
            reservation.changeStatus(ReservationStatus.CONFIRMED);

            // when
            ReservationStatus result = ReservationStatus.CONFIRMED.checkIn(reservation);

            // then
            assertThat(result).isEqualTo(ReservationStatus.CHECKED_IN);
        }

        @Test
        @DisplayName("체크인 날짜 이전에는 체크인 실패")
        void 체크인_날짜_이전_실패() {
            // given
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            Reservation futureReservation = new Reservation("김철수", tomorrow, tomorrow.plusDays(2), campsite);
            futureReservation.changeStatus(ReservationStatus.CONFIRMED);

            // when & then
            assertThatThrownBy(() -> ReservationStatus.CONFIRMED.checkIn(futureReservation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("체크인 날짜가 아직 되지 않았습니다");
        }

        @Test
        @DisplayName("체크인 기간 초과 시 체크인 실패")
        void 체크인_기간_초과_실패() {
            // given
            LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
            Reservation expiredReservation = new Reservation("이영희", threeDaysAgo, threeDaysAgo.plusDays(2), campsite);
            expiredReservation.changeStatus(ReservationStatus.CONFIRMED);

            // when & then
            assertThatThrownBy(() -> ReservationStatus.CONFIRMED.checkIn(expiredReservation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("체크인 기간이 지났습니다");
        }

        @ParameterizedTest
        @EnumSource(value = ReservationStatus.class, names = {"WAITING", "PENDING", "REJECTED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"})
        @DisplayName("CONFIRMED가 아닌 상태에서는 체크인 실패")
        void CONFIRMED가_아닌_상태_체크인_실패(ReservationStatus invalidStatus) {
            // given
            reservation.changeStatus(invalidStatus);

            // when & then
            assertThatThrownBy(() -> invalidStatus.checkIn(reservation))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("이미 체크인된 예약 재체크인 실패")
        void 이미_체크인된_예약_재체크인_실패() {
            // given
            reservation.changeStatus(ReservationStatus.CHECKED_IN);

            // when & then
            assertThatThrownBy(() -> ReservationStatus.CHECKED_IN.checkIn(reservation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 체크인된 예약입니다");
        }

        @Test
        @DisplayName("취소된 예약 체크인 실패")
        void 취소된_예약_체크인_실패() {
            // given
            reservation.changeStatus(ReservationStatus.CANCELLED);

            // when & then
            assertThatThrownBy(() -> ReservationStatus.CANCELLED.checkIn(reservation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("취소된 예약은 체크인할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("체크아웃 로직 테스트")
    class CheckOutLogicTest {

        @Test
        @DisplayName("CHECKED_IN 상태에서 체크아웃 성공")
        void CHECKED_IN_체크아웃_성공() {
            // given
            reservation.changeStatus(ReservationStatus.CHECKED_IN);

            // when
            ReservationStatus result = ReservationStatus.CHECKED_IN.checkOut(reservation);

            // then
            assertThat(result).isEqualTo(ReservationStatus.CHECKED_OUT);
        }

        @ParameterizedTest
        @EnumSource(value = ReservationStatus.class, names = {"WAITING", "PENDING", "CONFIRMED", "REJECTED", "CHECKED_OUT", "CANCELLED"})
        @DisplayName("CHECKED_IN이 아닌 상태에서는 체크아웃 실패")
        void CHECKED_IN이_아닌_상태_체크아웃_실패(ReservationStatus invalidStatus) {
            // given
            reservation.changeStatus(invalidStatus);

            // when & then
            assertThatThrownBy(() -> invalidStatus.checkOut(reservation))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("체크인하지 않은 예약 체크아웃 실패")
        void 체크인하지_않은_예약_체크아웃_실패() {
            // given
            reservation.changeStatus(ReservationStatus.CONFIRMED);

            // when & then
            assertThatThrownBy(() -> ReservationStatus.CONFIRMED.checkOut(reservation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("체크인하지 않은 예약은 체크아웃할 수 없습니다");
        }

        @Test
        @DisplayName("이미 체크아웃된 예약 재체크아웃 실패")
        void 이미_체크아웃된_예약_재체크아웃_실패() {
            // given
            reservation.changeStatus(ReservationStatus.CHECKED_OUT);

            // when & then
            assertThatThrownBy(() -> ReservationStatus.CHECKED_OUT.checkOut(reservation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 체크아웃된 예약입니다");
        }
    }

    @Nested
    @DisplayName("상태 전환 플로우 테스트")
    class StateTransitionFlowTest {

        @Test
        @DisplayName("정상적인 예약 플로우: WAITING → CONFIRMED → CHECKED_IN → CHECKED_OUT")
        void 정상_예약_플로우() {
            // given
            reservation.changeStatus(ReservationStatus.WAITING);

            // when & then - WAITING → CONFIRMED
            assertThatNoException().isThrownBy(() ->
                reservation.changeStatus(ReservationStatus.CONFIRMED));

            // when & then - CONFIRMED → CHECKED_IN
            ReservationStatus checkedInResult = reservation.getStatus().checkIn(reservation);
            assertThat(checkedInResult).isEqualTo(ReservationStatus.CHECKED_IN);

            // when & then - CHECKED_IN → CHECKED_OUT (수동 상태 변경)
            reservation.changeStatus(ReservationStatus.CHECKED_IN);
            ReservationStatus checkedOutResult = reservation.getStatus().checkOut(reservation);
            assertThat(checkedOutResult).isEqualTo(ReservationStatus.CHECKED_OUT);
        }

        @Test
        @DisplayName("취소 플로우: CONFIRMED → CANCELLED")
        void 취소_플로우() {
            // given
            reservation.changeStatus(ReservationStatus.CONFIRMED);

            // when & then
            assertThatNoException().isThrownBy(() ->
                reservation.changeStatus(ReservationStatus.CANCELLED));
        }

        @Test
        @DisplayName("거절 플로우: PENDING → REJECTED")
        void 거절_플로우() {
            // given
            reservation.changeStatus(ReservationStatus.PENDING);

            // when & then
            assertThatNoException().isThrownBy(() ->
                reservation.changeStatus(ReservationStatus.REJECTED));
        }
    }

    @Nested
    @DisplayName("엣지 케이스 테스트")
    class EdgeCaseTest {

        @Test
        @DisplayName("체크인 가능 기간 경계값 테스트")
        void 체크인_가능_기간_경계값() {
            // given - 체크인 날짜 당일
            LocalDate today = LocalDate.now();
            Reservation todayReservation = new Reservation("오늘", today, today.plusDays(2), campsite);
            todayReservation.changeStatus(ReservationStatus.CONFIRMED);

            // given - 체크인 날짜 2일 후 (마지막 가능한 날)
            LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
            Reservation borderlineReservation = new Reservation("경계", twoDaysAgo, twoDaysAgo.plusDays(2), campsite);
            borderlineReservation.changeStatus(ReservationStatus.CONFIRMED);

            // when & then - 모두 성공해야 함
            assertThatNoException().isThrownBy(() ->
                ReservationStatus.CONFIRMED.checkIn(todayReservation));
            assertThatNoException().isThrownBy(() ->
                ReservationStatus.CONFIRMED.checkIn(borderlineReservation));
        }

        @Test
        @DisplayName("체크인 불가능 경계값 테스트")
        void 체크인_불가능_경계값() {
            // given - 체크인 날짜 하루 전
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            Reservation tooEarlyReservation = new Reservation("너무이른", tomorrow, tomorrow.plusDays(2), campsite);
            tooEarlyReservation.changeStatus(ReservationStatus.CONFIRMED);

            // given - 체크인 날짜 3일 후 (불가능한 날)
            LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
            Reservation tooLateReservation = new Reservation("너무늦은", threeDaysAgo, threeDaysAgo.plusDays(2), campsite);
            tooLateReservation.changeStatus(ReservationStatus.CONFIRMED);

            // when & then - 모두 실패해야 함
            assertThatThrownBy(() -> ReservationStatus.CONFIRMED.checkIn(tooEarlyReservation))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> ReservationStatus.CONFIRMED.checkIn(tooLateReservation))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}