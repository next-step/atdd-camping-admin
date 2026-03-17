package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.enums.CampsiteStatus;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
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
    public Campsite createCampsite(CreateCampsiteRequest request) {
        if (campsiteRepository.findBySiteNumber(request.getSiteNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 siteNumber입니다: " + request.getSiteNumber());
        }
        Campsite campsite = new Campsite(
                request.getSiteNumber(),
                request.getDescription() != null ? request.getDescription() : "",
                request.getMaxPeople()
        );
        return campsiteRepository.save(campsite);
    }

    @Transactional
    public Campsite updateCampsite(Long id, UpdateCampsiteRequest request) {
        Campsite campsite = findById(id);
        if (request.getSiteNumber() != null) {
            campsite.setSiteNumber(request.getSiteNumber());
        }
        if (request.getDescription() != null) {
            campsite.setDescription(request.getDescription());
        }
        if (request.getMaxPeople() != null) {
            campsite.setMaxPeople(request.getMaxPeople());
        }
        return campsiteRepository.save(campsite);
    }

    @Transactional
    public Campsite updateStatus(Long id, String statusValue) {
        if (statusValue == null || statusValue.isBlank()) {
            throw new IllegalArgumentException("상태 값은 필수입니다.");
        }

        CampsiteStatus newStatus;
        try {
            newStatus = CampsiteStatus.valueOf(statusValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 캠프사이트 상태 값입니다: " + statusValue);
        }

        Campsite campsite = findById(id);
        campsite.setStatus(newStatus);
        return campsiteRepository.save(campsite);
    }

    private Campsite findById(Long id) {
        return campsiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + id));
    }
}
