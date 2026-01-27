package com.camping.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@NoArgsConstructor
public class UpdateReservationStatusRequest {
    @NotBlank(message = "상태 값은 비어 있을 수 없습니다")
    private String status;
}