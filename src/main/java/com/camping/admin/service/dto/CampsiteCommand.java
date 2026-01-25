package com.camping.admin.service.dto;

import com.camping.admin.domain.entity.Campsite;
import com.google.common.base.Preconditions;
import lombok.Getter;

@Getter
public class CampsiteCommand {
    private final String siteNumber;
    private final String description;
    private final Integer maxPeople;

    public CampsiteCommand(String siteNumber, String description, Integer maxPeople) {
        Preconditions.checkNotNull(siteNumber, "Site number must not be null.");
        Preconditions.checkNotNull(maxPeople, "Max people must not be null.");

        this.siteNumber = siteNumber;
        this.description = description == null ? "" : description;
        this.maxPeople = maxPeople;
    }

    public Campsite toEntity() {
        return new Campsite(siteNumber, description, maxPeople);
    }
}
