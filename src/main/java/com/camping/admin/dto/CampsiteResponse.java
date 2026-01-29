package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.Getter;

/**
 * 캠프사이트 응답 DTO
 * - 엔티티 직접 노출 대신 DTO 사용
 */
@Getter
public class CampsiteResponse {

    private final Long id;
    private final String siteNumber;
    private final String description;
    private final Integer maxPeople;

    private CampsiteResponse(Long id, String siteNumber, String description, Integer maxPeople) {
        this.id = id;
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public static CampsiteResponse from(Campsite campsite) {
        return new CampsiteResponse(
                campsite.getId(),
                campsite.getSiteNumber(),
                campsite.getDescription(),
                campsite.getMaxPeople()
        );
    }
}