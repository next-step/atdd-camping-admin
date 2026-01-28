package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteDto {

    private Long id;
    private String siteNumber;
    private String description;
    private Integer maxPeople;

    public static CampsiteDto from(Campsite campsite) {
        return new CampsiteDto(
                campsite.getId(),
                campsite.getSiteNumber(),
                campsite.getDescription(),
                campsite.getMaxPeople()
        );
    }
}
