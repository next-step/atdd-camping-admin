package com.camping.admin.dto;

import lombok.Data;

@Data
public class CreateCampsiteRequest {

    private String siteNumber;
    private String description;
    private Integer maxPeople;
}
