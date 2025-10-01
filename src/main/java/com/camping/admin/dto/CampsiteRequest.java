package com.camping.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteRequest {

    @NotBlank(message = "Site number is required")
    private String siteNumber;

    private String description;

    @Positive(message = "Max people must be positive")
    private Integer maxPeople;
}
