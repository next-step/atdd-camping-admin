package com.camping.admin.dto.request;

public record CampsiteRequest(
        String siteNumber,
        String description,
        Integer maxPeople
) {
}
