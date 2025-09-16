package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateReservationStatusRequest {
    ReservationStatus status;

    public void validate() {
        if (status == null || status == ReservationStatus.NONE) {
            throw new RuntimeException("Invalid reservation status");
        }
    }

    public static UpdateReservationStatusRequest from(ReservationStatus status) {
        return new UpdateReservationStatusRequest(status);
    }
}
