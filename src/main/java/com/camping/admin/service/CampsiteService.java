package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;
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

    // ===== Command =====

    @Transactional
    public Campsite create(CampsiteCreateRequest createReq) {
        Campsite newCampsite = Campsite.create(
                createReq.siteNumber(),
                createReq.description(),
                createReq.maxPeople());
        return campsiteRepository.save(newCampsite);
    }

    @Transactional
    public Campsite update(Long campsiteId, CampsiteUpdateRequest updateReq) {
        Campsite campsite = findById(campsiteId);
        campsite.update(
                updateReq.siteNumber(),
                updateReq.description(),
                updateReq.maxPeople());

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
