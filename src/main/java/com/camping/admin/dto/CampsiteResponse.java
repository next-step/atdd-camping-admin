package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;

public record CampsiteResponse(
        Long id,
        String siteNumber,
        String description,
        Integer maxPeople
) {
    public static CampsiteResponse of(Campsite campsite) {
        return new CampsiteResponse(
                campsite.getId(),
                campsite.getSiteNumber(),
                campsite.getDescription(),
                campsite.getMaxPeople()
        );
    }
}
