package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.exception.DuplicateSiteNumberException;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<Campsite> findAll() {
        return campsiteRepository.findAll();
    }

    @Transactional
    public Campsite create(CreateCampsiteRequest request) {
        validateCreateRequest(request);

        Campsite campsite = new Campsite(
                request.getSiteNumber(),
                request.getDescription() != null ? request.getDescription() : "",
                request.getMaxPeople()
        );

        return campsiteRepository.save(campsite);
    }

    @Transactional
    public Campsite update(Long campsiteId, UpdateCampsiteRequest request) {
        Campsite campsite = findById(campsiteId);

        if (request.getSiteNumber() != null) {
            campsite.setSiteNumber(request.getSiteNumber());
        }
        if (request.getDescription() != null) {
            campsite.setDescription(request.getDescription());
        }
        if (request.getMaxPeople() != null) {
            campsite.setMaxPeople(request.getMaxPeople());
        }

        return campsite;
    }

    public Campsite findById(Long campsiteId) {
        return campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));
    }

    private void validateCreateRequest(CreateCampsiteRequest request) {
        if (request.getSiteNumber() == null || request.getSiteNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Site number is required");
        }

        if (request.getMaxPeople() == null || request.getMaxPeople() <= 0) {
            throw new IllegalArgumentException("Max people must be greater than 0");
        }

        if (campsiteRepository.existsBySiteNumber(request.getSiteNumber())) {
            throw new DuplicateSiteNumberException("Site number already exists: " + request.getSiteNumber());
        }
    }
}
