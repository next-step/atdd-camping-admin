package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCampsiteRequest {
    private String siteNumber;
    private String description;
    private Integer maxPeople;
}
