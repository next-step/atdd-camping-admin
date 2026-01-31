package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCampsiteRequest {

    private String siteNumber;
    private String description;
    private Integer maxPeople;

    public CreateCampsiteRequest(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople != null ? maxPeople : null;
    }

    public Campsite toEntity() {
        Integer maxPeopleValue = maxPeople != null ? maxPeople : null;
        String descriptionValue = description != null ? description : "";
        return new Campsite(siteNumber, descriptionValue, maxPeopleValue);
    }
}