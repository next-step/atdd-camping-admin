package com.camping.admin.dto;

public record CampsiteCreateRequest(
        String siteNumber,
        String description,
        Integer maxPeople
) {
}
