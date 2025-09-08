package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CampsiteDto {
    private Long id;
    private String siteNumber;
    private String description;
    private Integer maxPeople;
    private List<ReservationDto> reservations;

    public CampsiteDto(Campsite campsite) {
        this.id = campsite.getId();
        this.siteNumber = campsite.getSiteNumber();
        this.description = campsite.getDescription();
        this.maxPeople = campsite.getMaxPeople();
        this.reservations = campsite.getReservations().stream()
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }
}
