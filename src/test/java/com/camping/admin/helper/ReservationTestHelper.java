package com.camping.admin.helper;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.common.CommonHooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.camping.admin.apiExtractableresponse.ReservationApiExtractableResponse.예약을_생성한다;
import static com.camping.admin.apiExtractableresponse.ReservationApiExtractableResponse.예약의_상태를_변경한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 예약 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class ReservationTestHelper {

    private static final Long 존재하지_않는_캠프사이트_ID = 999999L;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private CampsiteTestHelper campsiteTestHelper;

    private Reservation currentReservation;
    private Campsite currentCampsite;

    // ==================== 캠프사이트 설정 (Given) ====================

    public void 사이트번호로_캠프사이트를_준비한다(String siteNumber) {
        campsiteTestHelper.사이트번호로_캠프사이트를_생성한다(siteNumber);
        this.currentCampsite = campsiteRepository.findBySiteNumber(siteNumber)
                .orElseThrow(() -> new IllegalStateException("캠프사이트를 찾을 수 없습니다: " + siteNumber));
    }

    // ==================== 예약 설정 (Given) ====================

    public void 해당_캠프사이트에_예약을_생성한다(String startDate, String endDate) {
        Reservation reservation = new Reservation("사전예약고객", LocalDate.parse(startDate), LocalDate.parse(endDate), currentCampsite);
        reservationRepository.save(reservation);
    }

    // ==================== 예약 조회 (Given/When) ====================

    public Reservation 첫번째_예약을_조회한다() {
        return reservationRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("예약이 존재하지 않습니다."));
    }

    public void 확정된_예약을_찾는다() {
        this.currentReservation = reservationRepository.findAll().stream()
                .filter(r -> ReservationStatus.CONFIRMED.name().equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("확정 상태인 예약이 존재하지 않습니다."));
    }

    // ==================== API 호출 - 예약 생성 (When) ====================

    public void 고객명으로_예약을_요청한다(String customerName, String startDate, String endDate) {
        Map<String, Object> body = new HashMap<>();
        body.put("campsiteId", currentCampsite.getId());
        body.put("customerName", customerName);
        body.put("startDate", startDate);
        body.put("endDate", endDate);
        CommonHooks.lastResponse = 예약을_생성한다(body);
    }

    public void 존재하지_않는_캠프사이트에_예약을_요청한다() {
        Map<String, Object> body = new HashMap<>();
        body.put("campsiteId", 존재하지_않는_캠프사이트_ID);
        body.put("customerName", "테스트고객");
        body.put("startDate", "2025-06-01");
        body.put("endDate", "2025-06-03");
        CommonHooks.lastResponse = 예약을_생성한다(body);
    }

    public void 해당_캠프사이트에_날짜로_예약을_요청한다(String startDate, String endDate) {
        Map<String, Object> body = new HashMap<>();
        body.put("campsiteId", currentCampsite.getId());
        body.put("customerName", "테스트고객");
        body.put("startDate", startDate);
        body.put("endDate", endDate);
        CommonHooks.lastResponse = 예약을_생성한다(body);
    }

    public void 고객명_없이_예약을_요청한다() {
        Map<String, Object> body = new HashMap<>();
        body.put("campsiteId", currentCampsite.getId());
        body.put("startDate", "2025-06-01");
        body.put("endDate", "2025-06-03");
        CommonHooks.lastResponse = 예약을_생성한다(body);
    }

    // ==================== API 호출 - 예약 상태 변경 (When) ====================

    public void 예약_상태를_변경한다(Long reservationId, String status) {
        CommonHooks.lastResponse = 예약의_상태를_변경한다(reservationId, status);
    }

    public void 현재_예약의_상태를_변경한다(String status) {
        예약_상태를_변경한다(currentReservation.getId(), status);
    }

    // ==================== 검증 - 예약 생성 (Then) ====================

    public void 예약이_생성되었는지_검증한다() {
        assertThat(CommonHooks.lastResponse.statusCode()).isEqualTo(201);
    }

    public void 예약을_조회하여_고객명을_검증한다(String expectedCustomerName) {
        Long createdId = CommonHooks.lastResponse.jsonPath().getLong("id");
        Reservation reservation = reservationRepository.findById(createdId)
                .orElseThrow(() -> new AssertionError("생성된 예약을 찾을 수 없습니다. ID: " + createdId));
        assertThat(reservation.getCustomerName()).isEqualTo(expectedCustomerName);
    }

    // ==================== 검증 - 예약 상태 변경 (Then) ====================

    public void 예약을_조회하여_상태를_검증한다(String status) {
        assertThat(현재_예약을_다시_조회한다().getStatus())
                .isEqualTo(status);
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
