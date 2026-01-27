package com.camping.admin.unit;


import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.entity.ReservationStatusHistory;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.exception.InvalidStatusTransitionException;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.ReservationStatusHistoryRepository;
import com.camping.admin.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceUnitTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationStatusHistoryRepository reservationStatusHistoryRepository;

    @ParameterizedTest
    @CsvSource({
            "WAITING, PENDING",
            "PENDING, CONFIRMED",
            "CONFIRMED, CHECKED_IN",
            "CHECKED_IN, CHECKED_OUT"
    })
    @DisplayName("유효한 상태 변경 시, 변경 이력이 저장되어야 한다")
    void updateStatus_SavesHistory_ForValidTransitions(ReservationStatus from, ReservationStatus to) {
        // given
        long reservationId = 1L;
        Reservation reservation = new Reservation("테스터", LocalDate.now(), LocalDate.now().plusDays(1), new Campsite());
        reservation.setStatus(from.name());

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        // when
        reservationService.updateStatus(reservationId, to.name());

        // then
        ArgumentCaptor<ReservationStatusHistory> historyCaptor = ArgumentCaptor.forClass(ReservationStatusHistory.class);
        verify(reservationStatusHistoryRepository, times(1))
                .save(historyCaptor.capture());

        ReservationStatusHistory savedHistory = historyCaptor.getValue();
        assertThat(savedHistory.getOldStatus()).isEqualTo(from);
        assertThat(savedHistory.getNewStatus()).isEqualTo(to);
        assertThat(savedHistory.getReservation().getStatus()).isEqualTo(to.name());
    }

    @ParameterizedTest
    @CsvSource({
            "CANCELLED, CONFIRMED",
            "CHECKED_OUT, CHECKED_IN",
            "REJECTED, PENDING",
            "CONFIRMED, PENDING"
    })
    @DisplayName("유효하지 않은 상태 변경 시, 예외가 발생하고 이력은 저장되지 않아야 한다")
    void updateStatus_ThrowsException_ForInvalidTransitions(ReservationStatus from, ReservationStatus to) {
        // given
        long reservationId = 1L;
        Reservation reservation = new Reservation("테스터", LocalDate.now(), LocalDate.now().plusDays(1), new Campsite());
        reservation.setStatus(from.name());

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> reservationService.updateStatus(reservationId, to.name()))
                        .isInstanceOf(InvalidStatusTransitionException.class);

        verify(reservationStatusHistoryRepository, never())
                .save(any(ReservationStatusHistory.class));
    }
}
