package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CampsiteCreateRequest {
    private String siteNumber;
    private String description;
    private Integer maxPeople;

    public CampsiteCreateRequest(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public String getDescription() {
        return description != null ? description : "";
    }
}
