package com.camping.admin.service.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.Getter;

@Getter
public class CampsiteResponse {
    private final Long id;
    private final String siteNumber;
    private final String description;
    private final Integer maxPeople;

    public CampsiteResponse(Long id, String siteNumber, String description, Integer maxPeople) {
        this.id = id;
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public static CampsiteResponse from(Campsite campsite) {
        if (campsite == null) {
            return null;
        }
        return new CampsiteResponse(
                campsite.getId(),
                campsite.getSiteNumber(),
                campsite.getDescription(),
                campsite.getMaxPeople()
        );
    }
}
