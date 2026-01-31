package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;
import com.camping.admin.repository.CampsiteRepository;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteRepository campsiteRepository;

    @GetMapping
    public ResponseEntity<List<Campsite>> getAllCampsites() {
        List<Campsite> all = campsiteRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public ResponseEntity<Campsite> createCampsite(@RequestBody CampsiteCreateRequest createReq) {
        String siteNumber = createReq.siteNumber();
        String description = Objects.requireNonNullElse(createReq.description(), "");
        Integer maxPeople = createReq.maxPeople();

        Campsite newCampsite = new Campsite(siteNumber, description, maxPeople);
        Campsite saved = campsiteRepository.save(newCampsite);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody CampsiteUpdateRequest updateReq) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        String siteNumber = Objects.requireNonNullElse(updateReq.siteNumber(), campsite.getSiteNumber());
        String description = Objects.requireNonNullElse(updateReq.description(), campsite.getDescription());
        Integer maxPeople = Objects.requireNonNullElse(updateReq.maxPeople(), campsite.getMaxPeople());

        campsite.setSiteNumber(siteNumber);
        campsite.setDescription(description);
        campsite.setMaxPeople(maxPeople);

        campsiteRepository.save(campsite);

        return ResponseEntity.ok(campsite);
    }
}