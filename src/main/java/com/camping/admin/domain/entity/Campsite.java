package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.*;

@Entity
@Table(name = "campsites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static Campsite create(String siteNumber, String description, Integer maxPeople) {
        var campsite = new Campsite();

        campsite.siteNumber = siteNumber;
        campsite.description = requireNonNullElse(description, "");
        campsite.maxPeople = maxPeople;

        return campsite;
    }

    public void update(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = requireNonNullElse(siteNumber, this.siteNumber);
        this.description = requireNonNullElse(description, this.description);
        this.maxPeople = requireNonNullElse(maxPeople, this.maxPeople);
    }
}