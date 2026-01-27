package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusUpdateRequest {

    @NotNull(message = "상태값은 필수입니다")
    private ReservationStatus status;
}