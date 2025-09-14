package com.camping.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CampsiteCreateRequest {
    @NotBlank(message = "사이트 번호는 필수입니다")
    private String siteNumber;

    @NotBlank(message = "설명은 필수입니다")
    private String description;

    @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다")
    private int maxPeople;
}
