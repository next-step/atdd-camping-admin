package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.enums.CampsiteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteResponse {

    private Long id;
    private String siteNumber;
    private String description;
    private Integer maxPeople;
    private CampsiteStatus status;

    public static CampsiteResponse from(Campsite campsite) {
        return new CampsiteResponse(
                campsite.getId(),
                campsite.getSiteNumber(),
                campsite.getDescription(),
                campsite.getMaxPeople(),
                campsite.getStatus()
        );
    }
}
