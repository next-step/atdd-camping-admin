package com.camping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationStatusRequest {
    private String status;

    public boolean isValid() {
        return status != null && !status.isEmpty();
    }
}
