package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    // ===== Command =====

    @Transactional
    public Campsite create(CampsiteCreateRequest createReq) {
        String siteNumber = createReq.siteNumber();
        String description = Objects.requireNonNullElse(createReq.description(), "");
        Integer maxPeople = createReq.maxPeople();

        Campsite newCampsite = new Campsite(siteNumber, description, maxPeople);
        return campsiteRepository.save(newCampsite);
    }

    @Transactional
    public Campsite update(Long campsiteId, CampsiteUpdateRequest updateReq) {
        Campsite campsite = findById(campsiteId);

        String siteNumber = Objects.requireNonNullElse(updateReq.siteNumber(), campsite.getSiteNumber());
        String description = Objects.requireNonNullElse(updateReq.description(), campsite.getDescription());
        Integer maxPeople = Objects.requireNonNullElse(updateReq.maxPeople(), campsite.getMaxPeople());

        campsite.setSiteNumber(siteNumber);
        campsite.setDescription(description);
        campsite.setMaxPeople(maxPeople);

        return campsite;
    }

    // ===== Query =====

    public List<Campsite> getAll() {
        return campsiteRepository.findAll();
    }

    public Campsite get(Long campsiteId) {
        return findById(campsiteId);
    }

    // ===== Helper =====

    private Campsite findById(Long campsiteId) {
        return campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));
    }
}
