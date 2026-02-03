package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;

public record ReservationStatusUpdateRequest(
        ReservationStatus status
) {
}
