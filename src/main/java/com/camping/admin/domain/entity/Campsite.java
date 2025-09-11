package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campsites")
@Setter
@NoArgsConstructor
public class Campsite {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(unique = true, nullable = false)
    private String siteNumber;

    @Getter
    private String description;

    @Getter
    private Integer maxPeople;

    @OneToMany(mappedBy = "campsite", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
    
    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }
}
