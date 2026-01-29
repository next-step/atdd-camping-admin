package com.camping.admin.controller;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateStatusRequest;
import com.camping.admin.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 예약 관리 컨트롤러
 * - 컨트롤러는 HTTP 요청/응답만 담당
 * - 비즈니스 로직은 ReservationService에 위임
 */
@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    /**
     * 예약 상태 변경 API
     * - 컨트롤러: 요청/응답 처리만 담당 (HTTP 관심사)
     * - 서비스: 비즈니스 로직 및 트랜잭션 처리 위임
     */
    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @Valid @RequestBody UpdateStatusRequest request) {
        ReservationResponse response = reservationService.updateStatus(reservationId, request.getStatus());
        return ResponseEntity.ok(response);
    }
}