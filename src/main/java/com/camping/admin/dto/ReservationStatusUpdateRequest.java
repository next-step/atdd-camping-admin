package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationStatusUpdateRequest {
    private String status;

    public ReservationStatusUpdateRequest(String status) {
        this.status = status;
    }
}
