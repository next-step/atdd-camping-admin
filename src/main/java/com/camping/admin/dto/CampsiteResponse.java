package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import java.util.List;
import lombok.Data;

@Data
public class CampsiteResponse {

    private Long id;
    private String siteNumber;
    private String description;
    private Integer maxPeople;

    public static List<CampsiteResponse> from(List<Campsite> campsites) {
        return campsites.stream()
            .map(CampsiteResponse::from)
            .toList();
    }

    public static CampsiteResponse from(Campsite campsite) {
        var response = new CampsiteResponse();
        response.id = campsite.getId();
        response.siteNumber = campsite.getSiteNumber();
        response.description = campsite.getDescription();
        response.maxPeople = campsite.getMaxPeople();
        return response;
    }
}
