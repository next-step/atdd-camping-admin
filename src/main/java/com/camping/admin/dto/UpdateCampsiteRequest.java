package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCampsiteRequest {
    private String siteNumber;
    private String description;
    private Integer maxPeople;
}