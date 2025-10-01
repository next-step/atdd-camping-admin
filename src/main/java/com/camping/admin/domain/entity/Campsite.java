package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campsites")
@Getter
@NoArgsConstructor
public class Campsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String siteNumber;

    private String description;

    private Integer maxPeople;

    @OneToMany(mappedBy = "campsite", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public void updateCampsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }
}
