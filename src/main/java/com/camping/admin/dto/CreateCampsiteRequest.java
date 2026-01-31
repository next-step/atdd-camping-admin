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
    private String maxPeople;

    public CreateCampsiteRequest(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople != null ? maxPeople.toString() : null;
    }

    public Campsite toEntity() {
        Integer maxPeopleValue = parseMaxPeople();
        String descriptionValue = description != null ? description : "";
        return new Campsite(siteNumber, descriptionValue, maxPeopleValue);
    }

    private Integer parseMaxPeople() {
        if (maxPeople == null) {
            return null;
        }
        try {
            return Integer.parseInt(maxPeople);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}