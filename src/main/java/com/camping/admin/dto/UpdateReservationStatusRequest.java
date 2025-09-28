package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationStatusRequest {
    private ReservationStatus status;

    public boolean isValid() {
        return status != null;
    }
}
