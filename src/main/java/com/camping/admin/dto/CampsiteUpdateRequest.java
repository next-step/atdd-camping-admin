package com.camping.admin.dto;

public record CampsiteUpdateRequest(
        String siteNumber,
        String description,
        Integer maxPeople
) {
}
