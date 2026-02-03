package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteResponse;
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
    public Long create(CampsiteCreateRequest createReq) {
        var newCampsite = Campsite.create(
                createReq.siteNumber(),
                createReq.description(),
                createReq.maxPeople());
        campsiteRepository.save(newCampsite);

        return newCampsite.getId();
    }

    @Transactional
    public void update(Long campsiteId, CampsiteUpdateRequest updateReq) {
        var campsite = findById(campsiteId);
        campsite.update(
                updateReq.siteNumber(),
                updateReq.description(),
                updateReq.maxPeople());
    }

    // ===== Query =====

    public List<CampsiteResponse> getAll() {
        return campsiteRepository.findAll().stream()
                .map(CampsiteResponse::from)
                .toList();
    }

    public CampsiteResponse get(Long campsiteId) {
        return CampsiteResponse.from(findById(campsiteId));
    }

    // ===== Helper =====

    private Campsite findById(Long campsiteId) {
        return campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));
    }
}
