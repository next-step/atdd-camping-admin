package com.camping.admin.dto.request;

import com.camping.admin.domain.enums.ReservationStatus;

public record ReservationStatusRequest(
        ReservationStatus status
) {}
