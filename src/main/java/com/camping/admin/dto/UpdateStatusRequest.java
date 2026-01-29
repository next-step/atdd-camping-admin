package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예약 상태 변경 요청 DTO
 * - Map<String, Object> 대신 전용 DTO를 사용하여 타입 안전성 확보
 * - @Valid와 함께 사용하여 검증 로직을 컨트롤러에서 분리
 */
@Getter
@NoArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "상태값은 필수입니다.")
    private ReservationStatus status;
}
