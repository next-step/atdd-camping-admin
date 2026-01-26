package com.camping.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCampsiteRequest {
  @NotBlank(message = "사이트 번호는 필수입니다")
  private String siteNumber;

  private String description;

  @NotNull(message = "최대 인원은 필수입니다")
  @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다")
  private Integer maxPeople;
}
