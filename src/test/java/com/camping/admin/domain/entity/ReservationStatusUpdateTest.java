package com.camping.admin.domain.entity;

import com.camping.admin.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ReservationStatusUpdateTest {

    private Reservation reservation;
    private Campsite campsite;

    @BeforeEach
    void setUp() {
        campsite = new Campsite("A-01", "일반 캠프사이트", 4);
        reservation = new Reservation("김철수", 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 3), 
                campsite);
        reservation.setStatus("CONFIRMED");
    }

    @DisplayName("상태 업데이트 요청을 정상적으로 검증한다")
    @Test
    void validateStatusUpdateRequest_Success() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("status", "CANCELLED");

        // when & then
        assertThatNoException().isThrownBy(() -> Reservation.validateStatusUpdateRequest(body));
    }

    @DisplayName("null 요청 본문에 대해 예외가 발생한다")
    @Test
    void validateStatusUpdateRequest_NullBody_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Reservation.validateStatusUpdateRequest(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Request body cannot be empty");
    }

    @DisplayName("빈 요청 본문에 대해 예외가 발생한다")
    @Test
    void validateStatusUpdateRequest_EmptyBody_ThrowsException() {
        // given
        Map<String, Object> body = new HashMap<>();

        // when & then
        assertThatThrownBy(() -> Reservation.validateStatusUpdateRequest(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Request body cannot be empty");
    }

    @DisplayName("상태를 정상적으로 업데이트할 수 있다")
    @Test
    void updateStatus_Success() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("status", "CANCELLED");

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo("CANCELLED");
    }

    @DisplayName("다른 상태로 업데이트할 수 있다")
    @Test
    void updateStatus_DifferentStatus_Success() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("status", "COMPLETED");

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo("COMPLETED");
    }

    @DisplayName("상태가 null인 경우 업데이트하지 않는다")
    @Test
    void updateStatus_NullStatus_NoUpdate() {
        // given
        String originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("status", null);

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("상태가 빈 문자열인 경우 업데이트하지 않는다")
    @Test
    void updateStatus_BlankStatus_NoUpdate() {
        // given
        String originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "");

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("상태가 공백인 경우 업데이트하지 않는다")
    @Test
    void updateStatus_WhitespaceStatus_NoUpdate() {
        // given
        String originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "   ");

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("status 키가 없는 경우 업데이트하지 않는다")
    @Test
    void updateStatus_NoStatusKey_NoUpdate() {
        // given
        String originalStatus = reservation.getStatus();
        Map<String, Object> body = new HashMap<>();
        body.put("otherField", "value");

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo(originalStatus);
    }

    @DisplayName("숫자 값도 문자열로 변환하여 상태로 설정한다")
    @Test
    void updateStatus_NumericValue_ConvertsToString() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("status", 1);

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo("1");
    }

    @DisplayName("복잡한 객체도 문자열로 변환하여 상태로 설정한다")
    @Test
    void updateStatus_ComplexObject_ConvertsToString() {
        // given
        Map<String, Object> statusObject = new HashMap<>();
        statusObject.put("code", "PENDING");
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", statusObject);

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).contains("PENDING");
    }

    @DisplayName("여러 필드가 있어도 status만 업데이트한다")
    @Test
    void updateStatus_MultipleFields_OnlyUpdatesStatus() {
        // given
        String originalCustomerName = reservation.getCustomerName();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "UPDATED");
        body.put("customerName", "새 이름");
        body.put("phoneNumber", "010-1234-5678");

        // when
        reservation.updateStatus(body);

        // then
        assertThat(reservation.getStatus()).isEqualTo("UPDATED");
        assertThat(reservation.getCustomerName()).isEqualTo(originalCustomerName); // 변경되지 않음
    }
}