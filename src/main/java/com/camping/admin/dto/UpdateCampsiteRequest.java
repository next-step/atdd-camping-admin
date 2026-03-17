package com.camping.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCampsiteRequest {
    private String siteNumber;
    private String description;
    private Integer maxPeople;
}
