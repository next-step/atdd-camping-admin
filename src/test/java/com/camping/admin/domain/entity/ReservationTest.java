package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.domain.exception.DomainException;
import com.camping.admin.domain.vo.ReservationTiming;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        @ParameterizedTest(name = "{0} -> {1} 변경 가능")
        @CsvSource({
                "WAITING, CONFIRMED",
                "PENDING, CONFIRMED",
                "CONFIRMED, CHECKED_IN",
                "CHECKED_IN, CHECKED_OUT"
        })
        @DisplayName("최종 상태가 아니면 상태를 변경할 수 있다")
        void updateStatusSuccess(ReservationStatus from, ReservationStatus to) {
            reservation.setStatus(from);

            reservation.updateStatus(to);

            assertThat(reservation.getStatus()).isEqualTo(to);
        }

        @ParameterizedTest(name = "{0}에서 변경 시 예외 발생")
        @EnumSource(value = ReservationStatus.class, names = {"CANCELLED", "CHECKED_OUT", "REJECTED"})
        @DisplayName("최종 상태에서 변경하면 예외가 발생한다")
        void updateStatusFromFinal(ReservationStatus finalStatus) {
            reservation.setStatus(finalStatus);

            assertThatThrownBy(() -> reservation.updateStatus(ReservationStatus.CONFIRMED))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("null로 변경하면 예외가 발생한다")
        void updateStatusWithNull() {
            assertThatThrownBy(() -> reservation.updateStatus((ReservationStatus) null))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("문자열로 상태를 변경할 수 있다")
        void updateStatusWithString() {
            reservation.updateStatus("CHECKED_IN");

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
        }

        @Test
        @DisplayName("유효하지 않은 문자열로 변경하면 예외가 발생한다")
        void updateStatusWithInvalidString() {
            assertThatThrownBy(() -> reservation.updateStatus("INVALID"))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("calculateNights / calculateRevenue")
    class Calculate {

        @ParameterizedTest(name = "{0} ~ {1} = {2}박, 매출 {3}원")
        @CsvSource({
                "2024-01-01, 2024-01-01, 1, 50000",
                "2024-01-01, 2024-01-02, 1, 50000",
                "2024-01-01, 2024-01-03, 2, 100000",
                "2024-01-01, 2024-01-10, 9, 450000"
        })
        @DisplayName("숙박일수와 매출을 계산할 수 있다")
        void calculateNightsAndRevenue(String startStr, String endStr, long expectedNights, long expectedRevenue) {
            LocalDate start = LocalDate.parse(startStr);
            LocalDate end = LocalDate.parse(endStr);
            LocalDate reservationDate = start.minusDays(1);

            reservation.setTiming(new ReservationTiming(reservationDate, start, end));

            assertThat(reservation.calculateNights()).isEqualTo(expectedNights);
            assertThat(reservation.calculateRevenue()).isEqualByComparingTo(BigDecimal.valueOf(expectedRevenue));
        }
    }
}