package com.camping.admin.steps.campsite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CampsiteDetail(Long id, String siteNumber, String description, int maxPeople) {

}
