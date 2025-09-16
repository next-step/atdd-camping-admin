package com.camping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateReservationConfirmCodeRequest {
    private String confirmCode;

    public void validate() {
        if (confirmCode == null || confirmCode.trim().isEmpty()) {
            throw new RuntimeException("Invalid confirm code");
        }
        if (confirmCode.length() != 6) {
            throw new RuntimeException("Confirm code must be 6 characters");
        }
    }
}