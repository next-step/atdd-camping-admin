package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationStatusUpdateRequest {

    @NotNull(message = "예약 상태는 필수 값입니다.")
    private final ReservationStatus status;

    @JsonCreator
    public ReservationStatusUpdateRequest(ReservationStatus status) {
        this.status = status;
    }
}
