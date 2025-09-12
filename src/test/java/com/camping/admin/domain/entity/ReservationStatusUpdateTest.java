package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationStatusUpdateTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation(
                "김철수",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 3),
                new Campsite("A-01", "일반 캠프사이트", 4)
        );
        reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    @DisplayName("상태 업데이트 요청을 정상적으로 처리한다")
    @Test
    void updateStatus_ValidRequest_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "CANCELLED");

        assertThatNoException().isThrownBy(() -> reservation.updateStatus(body));
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @DisplayName("null 요청 본문에 대해 예외가 발생한다")
    @Test
    void updateStatus_NullBody_ThrowsException() {
        assertThatThrownBy(() -> reservation.updateStatus(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Request body cannot be empty");
    }

    @DisplayName("빈 요청 본문에 대해 예외가 발생한다")
    @Test
    void updateStatus_EmptyBody_ThrowsException() {
        Map<String, Object> body = new HashMap<>();

        assertThatThrownBy(() -> reservation.updateStatus(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Request body cannot be empty");
    }

    @DisplayName("상태를 정상적으로 업데이트할 수 있다")
    @Test
    void updateStatus_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "CANCELLED");

        reservation.updateStatus(body);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @DisplayName("다른 상태로 업데이트할 수 있다")
    @Test
    void updateStatus_DifferentStatus_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "REJECTED");

        reservation.updateStatus(body);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REJECTED);
    }

    @DisplayName("상태가 null인 경우 업데이트하지 않는다")
    @Test
    void updateStatus_NullStatus_NoUpdate() {
        ReservationStatus originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("status", null);

        reservation.updateStatus(body);

        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("상태가 빈 문자열인 경우 업데이트하지 않는다")
    @Test
    void updateStatus_BlankStatus_NoUpdate() {
        ReservationStatus originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "");

        reservation.updateStatus(body);

        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("상태가 공백인 경우 업데이트하지 않는다")
    @Test
    void updateStatus_WhitespaceStatus_NoUpdate() {
        ReservationStatus originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "   ");

        reservation.updateStatus(body);

        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("status 키가 없는 경우 업데이트하지 않는다")
    @Test
    void updateStatus_NoStatusKey_NoUpdate() {
        ReservationStatus originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("otherField", "value");

        reservation.updateStatus(body);

        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("여러 필드가 있어도 status만 업데이트한다")
    @Test
    void updateStatus_MultipleFields_OnlyUpdatesStatus() {
        String originalCustomerName = reservation.getCustomerName();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "REJECTED");
        body.put("customerName", "새 이름");
        body.put("phoneNumber", "010-1234-5678");

        reservation.updateStatus(body);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REJECTED);
        assertThat(reservation.getCustomerName()).isEqualTo(originalCustomerName); // 변경되지 않음
    }
}
