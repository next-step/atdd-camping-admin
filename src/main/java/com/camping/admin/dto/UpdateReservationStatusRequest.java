package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReservationStatusRequest {

    @NotNull
    private ReservationStatus status;

    public UpdateReservationStatusRequest(ReservationStatus status) {
        this.status = status;
    }
}