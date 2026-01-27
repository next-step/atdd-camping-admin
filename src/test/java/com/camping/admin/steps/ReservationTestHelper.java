package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.camping.admin.apiExtractableresponse.ReservationApiExtractableResponse.예약의_상태를_변경한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 예약 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class ReservationTestHelper {

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation currentReservation;
    private Response lastResponse;

    // ==================== 예약 조회 (Given/When) ====================

    public void 확정된_예약을_찾는다() {
        this.currentReservation = reservationRepository.findAll().stream()
                .filter(r -> ReservationStatus.CONFIRMED.name().equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("확정 상태인 예약이 존재하지 않습니다."));
    }

    // ==================== API 호출 (When) ====================

    public void 예약_상태를_변경한다(Long reservationId, String status) {
        this.lastResponse = 예약의_상태를_변경한다(reservationId, status);
    }

    public void 현재_예약의_상태를_변경한다(String status) {
        예약_상태를_변경한다(currentReservation.getId(), status);
    }

    // ==================== 검증 (Then) ====================

    public void 응답_상태_코드를_검증한다(int expectedStatusCode) {
        assertThat(lastResponse.statusCode()).isEqualTo(expectedStatusCode);
    }

    public void 예약_상태를_검증한다(String status) {
        assertThat(현재_예약을_다시_조회한다().getStatus())
                .isEqualTo(status);
    }

    public void 겹치는_예약이_없는지_검증한다() {
        assertThat(reservationRepository.findOverlappingReservations(
                currentReservation.getCampsite().getId(),
                currentReservation.getStartDate(),
                currentReservation.getEndDate()
        )).isEmpty();
    }

    // ==================== Private ====================

    private Reservation 현재_예약을_다시_조회한다() {
        return reservationRepository.findById(currentReservation.getId()).orElseThrow();
    }
}