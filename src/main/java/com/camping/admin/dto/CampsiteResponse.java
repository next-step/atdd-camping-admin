package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteResponse {

    private Long campsiteId;
    private String siteNumber;
    private String description;
    private int maxPeople;

    public static CampsiteResponse from(Campsite campsite) {
        return new CampsiteResponse(
                campsite.getId(),
                campsite.getSiteNumber(),
                campsite.getDescription(),
                campsite.getMaxPeople()
        );
    }
}
