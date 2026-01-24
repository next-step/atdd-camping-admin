package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campsites")
@Getter
@Setter
@NoArgsConstructor
public class Campsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String siteNumber;

    private String description;

    private Integer maxPeople;

    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description == null ? "" : description;
        this.maxPeople = maxPeople;
    }

    public void update(String siteNumber, String description, Integer maxPeople) {
        if (siteNumber != null) {
            this.siteNumber = siteNumber;
        }
        if (description != null) {
            this.description = description;
        }
        if (maxPeople != null) {
            this.maxPeople = maxPeople;
        }
    }
}