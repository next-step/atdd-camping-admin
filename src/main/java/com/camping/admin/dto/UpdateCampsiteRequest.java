package com.camping.admin.dto;

import lombok.Data;

@Data
public class UpdateCampsiteRequest {

    private String siteNumber;
    private String description;
    private Integer maxPeople;
}
