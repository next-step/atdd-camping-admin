package com.camping.admin.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateReservationStatusRequest(
        @NotBlank(message = "상태 값은 필수입니다.")
        String status
) {
}
