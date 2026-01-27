package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
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
        Campsite newCampsite = new Campsite(
                request.getSiteNumber(),
                request.getDescription(),
                request.getMaxPeople()
        );
        return campsiteRepository.save(newCampsite);
    }

    @Transactional
    public Campsite updateCampsite(Long campsiteId, UpdateCampsiteRequest request) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        campsite.update(request.getSiteNumber(), request.getDescription(), request.getMaxPeople());
        return campsite;
    }
}
