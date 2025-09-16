package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationStatusRequest {
    @NotNull(message = "Status must be provided.")
    private ReservationStatus status;
}
