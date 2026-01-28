package com.camping.admin.dto.response;

import com.camping.admin.domain.entity.Campsite;

public record CampsiteResponse(
        Long id,
        String siteNumber,
        String description,
        Integer maxPeople
) {
    public static CampsiteResponse from(Campsite entity) {
        return new CampsiteResponse(
                entity.getId(),
                entity.getSiteNumber(),
                entity.getDescription(),
                entity.getMaxPeople()
        );
    }
}
