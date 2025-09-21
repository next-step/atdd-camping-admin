package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 예약 상태 수정 request
 */
public record UpdateReservationStatusRequest(

        @NotNull(message = "예약 상태값은 필수입니다.")
        ReservationStatus status
) {
}
