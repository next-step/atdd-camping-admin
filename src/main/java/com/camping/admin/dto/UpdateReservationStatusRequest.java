package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReservationStatusRequest {
    private String status;

    public UpdateReservationStatusRequest(String status) {
        this.status = status;
    }
}
