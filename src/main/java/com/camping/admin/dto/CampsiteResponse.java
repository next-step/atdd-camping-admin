package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.Getter;

@Getter
public class CampsiteResponse {

    private Long id;
    private String siteNumber;
    private String description;
    private Integer maxPeople;

    public static CampsiteResponse from(Campsite campsite) {
        return new CampsiteResponse(campsite);
    }

    private CampsiteResponse(Campsite campsite) {
        this.id = campsite.getId();
        this.siteNumber = campsite.getSiteNumber();
        this.description = campsite.getDescription();
        this.maxPeople = campsite.getMaxPeople();
    }
}