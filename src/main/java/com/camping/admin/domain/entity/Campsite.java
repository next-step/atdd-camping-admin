package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    
    @OneToMany(mappedBy = "campsite", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
    
    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public void update(String siteNumber, String description, Integer maxPeople) {
        if (siteNumber == null || siteNumber.isBlank()) {
            throw new IllegalArgumentException("Site number is required.");
        }

        if (maxPeople != null && maxPeople <= 0) {
            throw new IllegalArgumentException("Max people must be greater than zero.");
        }

        if (description != null && description.trim().length() < 5) {
            throw new IllegalArgumentException("Description is too short (minimum 5 characters).");
        }

        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }
}