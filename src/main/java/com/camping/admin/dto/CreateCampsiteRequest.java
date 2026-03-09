package com.camping.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCampsiteRequest {

    @NotBlank
    private String siteNumber;

    private String description;

    @Positive
    private Integer maxPeople;
}
